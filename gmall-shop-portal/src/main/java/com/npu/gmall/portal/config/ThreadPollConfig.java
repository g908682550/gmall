package com.npu.gmall.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 配置当前系统的线程池信息
 */
@Configuration
public class ThreadPollConfig {

    @Bean("mainThreadPoolExecutor")
    public ThreadPoolExecutor mainThreadPoolExecutor(PoolProperties poolProperties){

        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(poolProperties.getQueueSize());

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(poolProperties.getCoreSize(), poolProperties.getMaximumPoolSize(), 10, TimeUnit.SECONDS, blockingQueue);

        return poolExecutor;
    }

    @Bean("otherThreadPoolExecutor")
    public ThreadPoolExecutor otherThreadPoolExecutor(PoolProperties poolProperties){
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(poolProperties.getQueueSize());

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(poolProperties.getCoreSize(), poolProperties.getMaximumPoolSize(), 10, TimeUnit.SECONDS, blockingQueue);

        return poolExecutor;
    }

}
