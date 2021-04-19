package com.example.bloomserver.task;

import com.alibaba.fastjson.JSON;
import com.example.bloominterface.pojo.BloomFilterSituation;
import com.example.bloomserver.tools.BloomFilter;

import java.io.*;

public class SchedulePersistRdb implements Runnable {


    private BloomFilter bloomFilter;
    private String rootPath;
    private long start = System.currentTimeMillis();
    private double lastCount = 0;
    private long thresholdTime = 100;
    private double threshold = 30;


    public SchedulePersistRdb(BloomFilter bloomFilter, String rootPath) {
        this.bloomFilter = bloomFilter;
        this.rootPath = rootPath;
        this.lastCount = bloomFilter.getInsertCount();
    }


    @Override
    public void run() {
        long now = System.currentTimeMillis();
        long time = (now - start) / 1000;
        long difference = (long) (bloomFilter.getInsertCount() - lastCount);
        if (difference > threshold || difference < 0) {
            try {
                persist();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (time > this.thresholdTime) {
                try {
                    persist();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void persist() throws IOException {
        BloomFilterSituation bloomFilterSituation = null;
        long words[] = null;
        synchronized (this.bloomFilter) {
            bloomFilterSituation = this.bloomFilter.getBloomFilterSituation();
            words = bloomFilter.getBitSet().getWords();
        }
        String jsonSituation = JSON.toJSONString(bloomFilterSituation);
        System.out.println(jsonSituation);
        File outputFile1 = new File(rootPath + "\\rdb\\situation.rdb");
        File outputFile2 = new File(rootPath + "\\rdb\\bitmap.rdb");

        FileOutputStream fileOutputStream1 = null;
        FileOutputStream fileOutputStream2 = null;

        try {
            fileOutputStream1 = new FileOutputStream(outputFile1);
            fileOutputStream2 = new FileOutputStream(outputFile2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedWriter bw1 = null;
        BufferedWriter bw2 = null;
        try {
            bw1 = new BufferedWriter(new OutputStreamWriter(fileOutputStream1));
            bw2 = new BufferedWriter(new OutputStreamWriter(fileOutputStream2));
            bw1.write(jsonSituation);
            for (int i = 0; i < words.length; ++i) {
                bw2.write(words[i] + "");
                bw2.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bw1.close();
            bw2.close();

        }

        this.lastCount = bloomFilterSituation.getInsertCount();
        this.start = System.currentTimeMillis();
    }


}
