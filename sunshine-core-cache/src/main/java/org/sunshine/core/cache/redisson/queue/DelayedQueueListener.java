package org.sunshine.core.cache.redisson.queue;

/**
 * @author Teamo
 * @since 2022/11/09
 */
public interface DelayedQueueListener<T> {

    /**
     * 是否启用
     *
     * @return boolean
     */
    default boolean isEnable() {
        return true;
    }

    /**
     * 队列键
     *
     * @return String
     */
    String delayedQueueKey();

    /**
     * 消费
     *
     * @param message Object
     * @throws Exception Exception
     */
    void consume(T message) throws Exception;

    /**
     * 发生异常时最终处理
     */
    default void whenExceptionFinally() {
    }
}
