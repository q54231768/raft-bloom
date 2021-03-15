package com.example.bloomserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Configuration
public class InterlizeScheduleThreadPool {

    //初始化定时线程池的bean对象
    @Bean
    public ExecutorService schedulePersistRdbService(){
        ExecutorService executorService= Executors.newScheduledThreadPool(1);
        return executorService;
    }



}
