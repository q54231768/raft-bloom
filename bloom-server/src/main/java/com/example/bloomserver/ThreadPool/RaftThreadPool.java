package com.example.bloomserver.ThreadPool;

import javafx.concurrent.ScheduledService;

import java.util.concurrent.*;

public class RaftThreadPool {

    //机器核心数
    private int cpuCount = Runtime.getRuntime().availableProcessors();

    //线程池最大线程数
    private int maxPoolSize = cpuCount * 2;

    private int queueSize = 1024;

    private long keepTime = 60000;

    private TimeUnit keepTimeUnit = TimeUnit.MILLISECONDS;

    private ScheduledExecutorService scheduledExecutorService;

    private ThreadPoolExecutor threadPoolExecutor;


    public void init(){
        scheduledExecutorService = getScheduleThreadPool();
        threadPoolExecutor = getThreadPool();
    }

    public ScheduledExecutorService getScheduleThreadPool() {
        return new ScheduledThreadPoolExecutor(cpuCount, new RaftThreadFactory());
    }


    public ThreadPoolExecutor getThreadPool() {
        System.out.println(cpuCount + " " + maxPoolSize);
        return new ThreadPoolExecutor(
                cpuCount,
                maxPoolSize,
                keepTime,
                keepTimeUnit,
                new LinkedBlockingQueue<>(queueSize),
                new RaftThreadFactory()
        );
    }


    public void scheduleAtFixedRate(Runnable runnable, long initDelay, long delay) {
        scheduledExecutorService.scheduleAtFixedRate(runnable, initDelay, delay, TimeUnit.MILLISECONDS);
    }

    public void scheduleWithFixedRate(Runnable runnable, long initDelay, long delay) {
        scheduledExecutorService.scheduleWithFixedDelay(runnable, initDelay, delay, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("unchecked")
    public <T> Future<T> submit(Callable r) {
        return threadPoolExecutor.submit(r);
    }


    static class RaftThreadFactory implements ThreadFactory {


        @Override
        public Thread newThread(Runnable r) {
            Thread t = new RaftThread("Raft thread", r);
            t.setDaemon(true);
            t.setPriority(5);
            return t;
        }
    }


}
