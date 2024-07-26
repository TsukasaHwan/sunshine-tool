package org.sunshine.core.cache;

import com.alibaba.fastjson2.JSON;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.sunshine.core.cache.stream.AbstractStreamMessage;

/**
 * @author Teamo
 * @since 2024/7/8
 */
public record RedisMQTemplateImpl(RedisTemplate<String, Object> redisTemplate)
        implements RedisMQTemplate {

    @Override
    public <T extends AbstractStreamMessage> RecordId send(T message) {
        return redisTemplate.opsForStream().add(
                Record.of(JSON.toJSONString(message)).withStreamKey(message.getStreamKey())
        );
    }

    @Override
    public <T extends AbstractStreamMessage> RecordId send(RecordId recordId, T message) {
        return redisTemplate.opsForStream().add(
                Record.of(JSON.toJSONString(message))
                        .withId(recordId)
                        .withStreamKey(message.getStreamKey())
        );
    }

}
