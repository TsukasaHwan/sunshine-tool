package org.sunshine.core.cache.stream;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.sunshine.core.cache.RedisMQTemplate;
import org.sunshine.core.cache.support.scheduling.DistributedTaskScheduling;
import org.sunshine.core.tool.util.BeanUtils;
import org.sunshine.core.tool.util.TypeUtils;

import java.lang.reflect.Type;

/**
 * @author Teamo
 * @since 2023/5/26
 */
public abstract class AbstractStreamListener<T extends AbstractStreamMessage>
        implements StreamListener<String, ObjectRecord<String, String>> {

    private static final String LOCK_KEY = "lock:scheduled:redis:trim:%s";

    private RedisMQTemplate redisMQTemplate;

    private final Class<T> messageType;

    private final String streamKey;

    /**
     * Redis 消费组，默认使用 spring.application.name 名字
     */
    @Value("${spring.application.name}")
    private String group;

    protected AbstractStreamListener() {
        this.messageType = getMessageClass();
        this.streamKey = BeanUtils.newInstance(this.messageType).getStreamKey();
    }

    public abstract void onMessage(T message);

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        T messageObj = JSON.parseObject(message.getValue(), messageType);
        this.onMessage(messageObj);
        redisMQTemplate.redisTemplate().opsForStream().acknowledge(group, message);
    }

    /**
     * 清理消息队列（定时任务）
     *
     * @param count 保留数量
     */
    protected void trim(long count) {
        DistributedTaskScheduling scheduling = () -> redisMQTemplate.redisTemplate().opsForStream().trim(streamKey, count);
        String key = String.format(LOCK_KEY, streamKey);
        scheduling.execute(key);
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

    public void setRedisMQTemplate(RedisMQTemplate redisMQTemplate) {
        this.redisMQTemplate = redisMQTemplate;
    }
}
