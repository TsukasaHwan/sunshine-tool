package org.sunshine.core.cache.redisson.queue;

import org.redisson.api.RedissonClient;

import java.util.Collection;

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
     * 发送消息
     *
     * @param record 消息
     * @param <T>    消息类型
     */
    <T> void send(DelayedRecord<T> record);

    /**
     * 移除消息
     *
     * @param queue 队列
     * @param value 消息
     * @param <T>   消息类型
     * @return 是否移除成功
     */
    <T> boolean remove(String queue, T value);

    /**
     * 移除消息列表
     *
     * @param queue  队列名称
     * @param values 消息列表
     * @param <T>    消息类型
     * @return 是否移除成功
     */
    <T> boolean removeAll(String queue, Collection<T> values);
}
