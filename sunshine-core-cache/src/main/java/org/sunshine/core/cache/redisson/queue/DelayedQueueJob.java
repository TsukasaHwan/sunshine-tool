package org.sunshine.core.cache.redisson.queue;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2022/11/09
 */
public interface DelayedQueueJob<T> {

    Logger log = LoggerFactory.getLogger(DelayedQueueJob.class);

    /**
     * 延迟线程池名称
     */
    ThreadFactory NAMED_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("delayed-queue-pool-%d").build();

    /**
     * 延迟队列线程池
     */
    ThreadPoolExecutor DELAYED_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,
            Math.max(Runtime.getRuntime().availableProcessors() * 2 * 4, 256),
            10L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(200),
            NAMED_THREAD_FACTORY,
            new ThreadPoolExecutor.AbortPolicy()
    );

    /**
     * 是否启用
     *
     * @return boolean
     */
    default boolean isEnable() {
        return true;
    }

    /**
     * 启动
     *
     * @param redissonClient RedissonClient
     */
    default void start(RedissonClient redissonClient) {
        if (!isEnable()) {
            return;
        }
        DELAYED_THREAD_POOL_EXECUTOR.execute(() -> {
            RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(dequeKey());
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    T take = blockingDeque.take();
                    consume(take);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    whenExceptionFinally();
                }
            }
        });
    }

    /**
     * 队列键
     *
     * @return String
     */
    String dequeKey();

    /**
     * 消费
     *
     * @param take Object
     * @throws Exception Exception
     */
    void consume(T take) throws Exception;

    /**
     * 发生异常时最终处理
     */
    default void whenExceptionFinally() {
    }
}
