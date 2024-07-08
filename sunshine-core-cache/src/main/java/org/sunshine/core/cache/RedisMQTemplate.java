package org.sunshine.core.cache;

import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.sunshine.core.cache.stream.AbstractStreamMessage;

/**
 * @author Teamo
 * @since 2024/7/8
 */
public interface RedisMQTemplate {

    /**
     * 获取RedisTemplate。
     *
     * @return {@link RedisTemplate}
     */
    RedisTemplate<String, Object> redisTemplate();

    /**
     * 发送消息。
     *
     * @param message 消息{@link AbstractStreamMessage}
     * @param <T>     消息类型
     * @return {@link RecordId}
     */
    <T extends AbstractStreamMessage> RecordId send(T message);
}
