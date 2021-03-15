package com.example.bloomserver.config;


import com.alibaba.fastjson.JSON;

import com.example.bloomserver.task.SchedulePersistRdb;
import com.example.bloomserver.tools.BloomFilter;
import com.example.bloomserver.tools.pojo.BloomFilterSituation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class InterlizeBloomFilter {


    private String rootPath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
    @Autowired
    private ExecutorService schedulePersistRdbService;

    /**
     * 初始化布隆过滤器的bean对象,为布隆过滤器的初始化过程
     *
     * @param expectedQuantityStr 为布隆过滤器的预期插入量
     * @param misjudgmentRateStr  为布隆过滤器的期望误判率
     */
    @Bean
    public BloomFilter bloomFilter(@Value("${BloomFilter.expectedQuantity:10000}") String expectedQuantityStr,
                                   @Value("${BloomFilter.misjudgmentRate:0.1}") String misjudgmentRateStr) throws IOException {
        BloomFilter bloomFilter = null;

        String dirPath = rootPath + "/rdb";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        String situationPath = rootPath + "/rdb/" + "situation.rdb";
        File situationFile = new File(situationPath);
        if (!situationFile.exists()) {
            situationFile.createNewFile();
        }

        String bitmapPath = rootPath + "/rdb/" + "bitmap.rdb";
        File bitmapFile = new File(bitmapPath);
        if (!bitmapFile.exists()) {
            bitmapFile.createNewFile();
        }

        FileInputStream sIn = null;
        Reader reader = new FileReader(situationFile);
        sIn = new FileInputStream(situationFile);
        Long fileLength = situationFile.length();

        //根据持久化文件初始化布隆过滤器
        if (fileLength.longValue() != 0L) {
            byte[] bytes = new byte[fileLength.intValue()];
            sIn.read(bytes);
            sIn.close();
            //获取布隆过滤器状态持久化信息
            String jsonStr = new String(bytes, "UTF-8");
            BloomFilterSituation bloomFilterSituation = JSON.parseObject(jsonStr, BloomFilterSituation.class);

            //获取布隆过滤器位图持久化信息
            FileInputStream bIn = new FileInputStream(bitmapFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(bIn));
            int wordsLength = ((bloomFilterSituation.getBitSize() - 1) >> 6) + 1;
            long words[] = new long[wordsLength];
            String line = br.readLine();
            for (int i = 0; line != null && i < wordsLength; line = br.readLine(), ++i) {
                words[i] = Long.parseLong(line);
            }
            br.close();
            bloomFilter = new BloomFilter(bloomFilterSituation, words);
        } else {//根据初始化参数初始化布隆过滤器
            int expectedQuantity = Integer.parseInt(expectedQuantityStr);
            Double misjudgmentRate = Double.parseDouble(misjudgmentRateStr);
            bloomFilter = new BloomFilter(expectedQuantity, misjudgmentRate);
        }

        //为定时线程池添加定时持久任务
        SchedulePersistRdb schedulePersistRdb = new SchedulePersistRdb(bloomFilter, rootPath);
        ((ScheduledExecutorService) schedulePersistRdbService).scheduleAtFixedRate(
                schedulePersistRdb,
                2,
                2,
                TimeUnit.SECONDS);
        return bloomFilter;
    }


}
