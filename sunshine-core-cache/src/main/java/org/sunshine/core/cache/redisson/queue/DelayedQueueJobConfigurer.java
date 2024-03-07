package org.sunshine.core.cache.redisson.queue;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2024/3/7
 */
public class DelayedQueueJobConfigurer implements InitializingBean, DisposableBean {

    private ThreadPoolExecutor delayedThreadPoolExecutor;

    private final List<DelayedQueueJob<?>> delayedQueueJobList;

    private final RedissonClient redissonClient;

    public DelayedQueueJobConfigurer(List<DelayedQueueJob<?>> delayedQueueJobList, RedissonClient redissonClient) {
        this.delayedQueueJobList = delayedQueueJobList;
        this.redissonClient = redissonClient;
    }

    @Override
    public void destroy() throws Exception {
        if (delayedThreadPoolExecutor != null) {
            delayedThreadPoolExecutor.shutdownNow();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(delayedQueueJobList, "delayedQueueJobList must be not empty!");

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("delayed-queue-pool-%d").build();
        int numberOfJob = delayedQueueJobList.size();
        delayedThreadPoolExecutor = new ThreadPoolExecutor(
                numberOfJob,
                numberOfJob,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(numberOfJob),
                namedThreadFactory
        );
        delayedQueueJobList.forEach(delayedQueueJob -> delayedThreadPoolExecutor.execute(() -> delayedQueueJob.start(redissonClient)));
    }
}
