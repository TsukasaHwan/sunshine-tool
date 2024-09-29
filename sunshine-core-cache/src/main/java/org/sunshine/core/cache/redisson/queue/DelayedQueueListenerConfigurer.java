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
public class DelayedQueueListenerConfigurer implements InitializingBean, DisposableBean {

    private ThreadPoolExecutor delayedThreadPoolExecutor;

    private final List<DelayedQueueListener<?>> delayedQueueListenerList;

    private final RedissonClient redissonClient;

    public DelayedQueueListenerConfigurer(List<DelayedQueueListener<?>> delayedQueueListenerList, RedissonClient redissonClient) {
        this.delayedQueueListenerList = delayedQueueListenerList;
        this.redissonClient = redissonClient;
    }

    @Override
    public void destroy() throws Exception {
        if (delayedThreadPoolExecutor == null) {
            return;
        }
        delayedThreadPoolExecutor.shutdownNow();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(delayedQueueListenerList, "delayedQueueListenerList must not be empty!");

        int numberOfJob = delayedQueueListenerList.stream().filter(DelayedQueueListener::isEnable).toList().size();
        if (numberOfJob == 0) {
            return;
        }
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("delayed-queue-thread-%d").build();
        delayedThreadPoolExecutor = new ThreadPoolExecutor(
                numberOfJob,
                numberOfJob,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(numberOfJob),
                namedThreadFactory
        );
        delayedQueueListenerList.forEach(delayedQueueListener -> delayedThreadPoolExecutor.execute(new DelayedQueuePollTask<>(redissonClient, delayedQueueListener)));
    }
}
