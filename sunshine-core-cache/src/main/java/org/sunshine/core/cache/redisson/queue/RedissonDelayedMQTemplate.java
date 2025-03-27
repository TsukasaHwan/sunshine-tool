package org.sunshine.core.cache.redisson.queue;

import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2025/3/27
 */
public interface RedissonDelayedMQTemplate {

    /**
     * 获取Redisson客户端
     *
     * @return Redisson客户端
     */
    RedissonClient redissonClient();

    /**
     * 发送延时消息
     *
     * @param queueName 队列名称
     * @param message   消息
     * @param delay     延时
     * @param timeUnit  时间单位
     * @param <T>       消息类型
     */
    <T> void send(String queueName, T message, long delay, TimeUnit timeUnit);

    /**
     * 移除消息
     *
     * @param queueName 队列名称
     * @param message   消息
     * @param <T>       消息类型
     * @return 是否移除成功
     */
    <T> boolean remove(String queueName, T message);
}
