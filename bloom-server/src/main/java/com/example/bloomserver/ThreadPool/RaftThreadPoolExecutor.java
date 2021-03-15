package com.example.bloomserver.ThreadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class RaftThreadPoolExecutor extends ThreadPoolExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RaftThreadPoolExecutor.class);

    private static final ThreadLocal<Long> THREAD_START_WORK_TIME = ThreadLocal.withInitial(System::currentTimeMillis);

    public RaftThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);

    }


    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        THREAD_START_WORK_TIME.get();
        LOGGER.debug("raft thread pool before Execute");
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        LOGGER.debug("raft thread pool after Execute, cost time : {}", System.currentTimeMillis() - THREAD_START_WORK_TIME.get());
        THREAD_START_WORK_TIME.remove();
    }

    @Override
    protected void terminated() {
        LOGGER.info("active thread count : {}, queueSize : {}, poolSize : {}", getActiveCount(), getQueue().size(), getPoolSize());
    }


}
