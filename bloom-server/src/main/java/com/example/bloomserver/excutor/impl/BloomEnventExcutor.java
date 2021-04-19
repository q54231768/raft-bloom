package com.example.bloomserver.excutor.impl;

import com.alibaba.fastjson.JSON;
import com.example.bloominterface.common.CommandType;
import com.example.bloominterface.common.ResultCode;
import com.example.bloominterface.exception.GlobalLoggerHandler;
import com.example.bloominterface.pojo.BloomFilterConfigMap;
import com.example.bloominterface.pojo.BloomFilterSituation;
import com.example.bloominterface.pojo.ClientCommandRes;
import com.example.bloominterface.pojo.LogEntry;
import com.example.bloomserver.excutor.EnventExcutor;
import com.example.bloomserver.tools.BloomFilter;
import org.slf4j.Logger;

public class BloomEnventExcutor implements EnventExcutor {

    private BloomFilter bloomFilter;

    //    private Logger LOGGER = LoggerFactory.getLogger(BloomEnventExcutor.class);
    private Logger LOGGER = null;

    public BloomEnventExcutor(BloomFilter bloomFilter) {
        this.bloomFilter = bloomFilter;
    }

    @Override
    public ClientCommandRes writeIn(LogEntry logEntry) {
        ClientCommandRes clientCommandRes = new ClientCommandRes(ResultCode.fail);
        try {
            if (logEntry.getCommandType() == CommandType.WRITE) {
                String value = logEntry.getValue();
                bloomFilter.put(value);
                clientCommandRes.setResult(ResultCode.success);
            } else if (logEntry.getCommandType() == CommandType.RESET) {
                BloomFilterConfigMap bloomFilterConfigMap = JSON.parseObject(logEntry.getValue(), BloomFilterConfigMap.class);
                int expectedQuantity = Integer.parseInt(bloomFilterConfigMap.getExpectedQuantityStr());
                double misjudgmentRate = Double.parseDouble(bloomFilterConfigMap.getMisjudgmentRateStr());
                bloomFilter.reset(expectedQuantity, misjudgmentRate);
                clientCommandRes.setResult(ResultCode.success);
            }
        } catch (Exception e) {
            GlobalLoggerHandler.handle(e, LOGGER);
        }
        return clientCommandRes;
    }

    @Override
    public ClientCommandRes read(Object paramter) {
        String value = (String) paramter;
        ClientCommandRes clientCommandRes = new ClientCommandRes(ResultCode.fail);
        try {
           boolean isMigahtContain = bloomFilter.mightContain(value);
           if(isMigahtContain){
               clientCommandRes.setResult(ResultCode.success);
           }else{
               clientCommandRes.setResult(ResultCode.fail);
           }
        } catch (Exception e) {
            GlobalLoggerHandler.handle(e, LOGGER);
        }
        return clientCommandRes;
    }

    @Override
    public ClientCommandRes getServerDeatil(Object paramter) {
        ClientCommandRes clientCommandRes = new ClientCommandRes(ResultCode.fail);
        try {
           BloomFilterSituation bloomFilterSituation = bloomFilter.getBloomFilterSituation();
           clientCommandRes.setResult(ResultCode.success);
           clientCommandRes.setDetails(bloomFilterSituation);
        } catch (Exception e) {
            GlobalLoggerHandler.handle(e, LOGGER);
        }
        return clientCommandRes;
    }
}
