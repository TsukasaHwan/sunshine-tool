package org.sunshine.core.cache.stream;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.sunshine.core.cache.RedisMQTemplate;
import org.sunshine.core.tool.util.BeanUtils;
import org.sunshine.core.tool.util.TypeUtils;

import java.lang.reflect.Type;

/**
 * @author Teamo
 * @since 2023/5/26
 */
public abstract class AbstractStreamListener<T extends AbstractStreamMessage>
        implements StreamListener<String, ObjectRecord<String, String>> {

    private final Class<T> messageType;
    private final String streamKey;
    private final AbstractStreamMessage.TrimConfig trimConfig;
    private final AbstractStreamMessage.DeadLetterConfig deadLetterConfig;
    private RedisMQTemplate redisMQTemplate;
    /**
     * Redis 消费组，默认使用 spring.application.name 名字
     */
    @Value("${spring.application.name}")
    private String group;

    protected AbstractStreamListener() {
        this.messageType = getMessageClass();
        T streamMessage = BeanUtils.newInstance(this.messageType);
        this.streamKey = streamMessage.getStreamKey();
        this.trimConfig = streamMessage.getTrimConfig();
        this.deadLetterConfig = streamMessage.getDeadLetterConfig();
    }

    public abstract void onMessage(T message);

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        T messageObj = JSON.parseObject(message.getValue(), messageType);
        this.onMessage(messageObj);
        redisMQTemplate.redisTemplate().opsForStream().acknowledge(group, message);
    }

    @SuppressWarnings("unchecked")
    private Class<T> getMessageClass() {
        Type type = TypeUtils.getTypeArgument(getClass(), 0);
        if (type == null) {
            throw new IllegalStateException(String.format("类型(%s) 需要设置消息类型", getClass().getName()));
        }
        return (Class<T>) type;
    }

    public String getStreamKey() {
        return streamKey;
    }

    public String getGroup() {
        return group;
    }

    public AbstractStreamMessage.TrimConfig getTrimConfig() {
        return trimConfig;
    }

    public AbstractStreamMessage.DeadLetterConfig getDeadLetterConfig() {
        return deadLetterConfig;
    }

    public void setRedisMQTemplate(RedisMQTemplate redisMQTemplate) {
        this.redisMQTemplate = redisMQTemplate;
    }
}
