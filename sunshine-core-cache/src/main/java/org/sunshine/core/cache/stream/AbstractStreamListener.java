package org.sunshine.core.cache.stream;

import com.alibaba.fastjson2.JSON;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
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
        implements StreamListener<String, @NonNull ObjectRecord<String, String>> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractStreamListener.class);

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

    /**
     * Spring消息监听器接口实现（处理原始Redis消息）
     *
     * <p>执行流程：</p>
     * 1. 将JSON字符串反序列化为具体消息类型
     * 2. 调用 {@link #onMessage(T)} 进行业务处理
     * 3. 自动确认消息（通过acknowledge接口）
     *
     * @param message Redis原始消息对象
     */
    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        T messageObj = JSON.parseObject(message.getValue(), messageType);
        this.onMessage(messageObj);
        redisMQTemplate.redisTemplate().opsForStream().acknowledge(group, message);
    }

    /**
     * 消息处理核心方法（必须由子类实现）
     *
     * @param message 需要处理的消息对象
     */
    public abstract void onMessage(T message);

    /**
     * 处理缺失消息的回调方法（当消息记录不存在时触发）
     * <p>功能说明：</p>
     * 1. 记录警告日志："[RECORD_NOT_FOUND] 未找到消息记录{}自动确认，流键：{}"
     * 2. 自动确认 Redis 流消息（通过 acknowledge 接口）
     * 3. 适用于消息记录丢失的异常场景（例如：Stream被trim或消息记录丢失）
     *
     * <p>使用场景：</p>
     * 当从 Redis 流中读取消息时，若指定 RecordId 对应的消息记录不存在，将调用此方法进行处理
     *
     * <p>扩展建议：</p>
     * 子类可通过覆盖此方法实现定制化逻辑，例如：
     * - 发送告警通知（携带 pendingMessage 的详细信息）
     * - 记录业务指标（如统计丢失消息数量）
     * - 执行降级策略（如记录失败日志或重试逻辑）
     *
     * @param pendingMessage 包含缺失消息元数据的PendingMessage对象（包含消息ID、消费者组等信息）
     */
    protected void handleMissingMessage(PendingMessage pendingMessage) {
        logger.warn("[RECORD_NOT_FOUND] 未找到消息记录{}自动确认，流键：{}", pendingMessage.getId(), streamKey);
        redisMQTemplate.redisTemplate().opsForStream().acknowledge(streamKey, group, pendingMessage.getId());
    }

    /**
     * 通过反射获取泛型消息类型
     *
     * @return 消息类型Class对象
     * @throws IllegalStateException 若泛型类型未正确设置时抛出
     */
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
