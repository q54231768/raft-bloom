package com.example.bloomserver.tools;

import java.text.DecimalFormat;

public class BloomFilterMonitor {


    //监控的布隆过滤器
    private BloomFilter bloomFilter;


    public BloomFilterMonitor() {

    }


    public void getSituation() {
        if (bloomFilter == null) {
            System.out.println("当前未绑定布隆过滤器");
            return;
        }
        String columnNames[] = {"当前插入次数", "当前冲突次数", "当前碰撞率"};
        DecimalFormat df = new DecimalFormat("#.0000000000");
        String columnValues[] = {bloomFilter.getInsertCount() + "",
                                  bloomFilter.getCollideCount() + "",
                                   "0"+df.format(bloomFilter.getCollisionProbability())};
        AlignOutput.output(columnNames);
        AlignOutput.output(columnValues);

    }


    public void bindBloomFilter(BloomFilter bloomFilter) {
        this.bloomFilter = bloomFilter;
    }


}
