package org.sunshine.core.cache.stream;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.stream.StreamListener;
import org.sunshine.core.cache.RedisMQTemplate;
import org.sunshine.core.tool.util.BeanUtils;
import org.sunshine.core.tool.util.TypeUtils;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teamo
 * @since 2023/5/26
 */
public abstract class AbstractStreamListener<T extends AbstractStreamMessage>
        implements StreamListener<String, ObjectRecord<String, String>> {

    private final static Logger log = LoggerFactory.getLogger(AbstractStreamListener.class);

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
     * 清理消息队列
     *
     * @param count 保留数量
     */
    protected void trimStream(long count) {
        redisMQTemplate.redisTemplate().opsForStream().trim(streamKey, count);
    }

    /**
     * 处理死信
     */
    protected void handleDeadLetter() {
        // 处理死信队列
        // 获取group中的pending消息信息
        PendingMessagesSummary pendingMessagesSummary = redisMQTemplate.redisTemplate().opsForStream().pending(streamKey, group);
        if (pendingMessagesSummary == null) {
            return;
        }
        // 所有pending消息的数量
        long totalPendingMessages = pendingMessagesSummary.getTotalPendingMessages();
        if (totalPendingMessages == 0) {
            return;
        }
        // 获取每个消费者的pending消息数量
        Map<String, Long> pendingMessagesPerConsumer = pendingMessagesSummary.getPendingMessagesPerConsumer();
        Map<String, List<RecordId>> oldConsumerTransferMessageMap = new HashMap<>();
        // 遍历每个消费者中的pending消息
        pendingMessagesPerConsumer.forEach((consumer, consumerTotalPendingMessages) -> {
            // 待重试的 RecordId
            if (consumerTotalPendingMessages > 0) {
                // 读取消费者pending队列的前10条记录，从ID=0的记录开始，一直到ID最大值
                PendingMessages pendingMessages = redisMQTemplate.redisTemplate().opsForStream().pending(streamKey, Consumer.from(group, consumer), Range.closed("0", "+"), 10);
                // 遍历所有pending消息的详情
                List<RecordId> transferGroupMessageList = new ArrayList<>(pendingMessages.size());
                pendingMessages.forEach(pendingMessage -> handleTransferGroupList(pendingMessage, transferGroupMessageList));
                if (transferGroupMessageList.size() > 0) {
                    oldConsumerTransferMessageMap.put(consumer, transferGroupMessageList);
                }
            }
        });

        changeConsumer(oldConsumerTransferMessageMap);
    }

    /**
     * 处理待转移消息列表
     *
     * @param pendingMessage           挂起的消息
     * @param transferGroupMessageList 待转移的RecordId
     */
    protected void handleTransferGroupList(PendingMessage pendingMessage, List<RecordId> transferGroupMessageList) {
        // 消息的ID
        RecordId recordId = pendingMessage.getId();
        // 消息从消费组中获取，到此刻的时间
        Duration elapsedTimeSinceLastDelivery = pendingMessage.getElapsedTimeSinceLastDelivery();
        // 消息被获取的次数
        long deliveryCount = pendingMessage.getTotalDeliveryCount();
        if (elapsedTimeSinceLastDelivery.getSeconds() > 20 && deliveryCount == 1) {
            transferGroupMessageList.add(recordId);
        } else {
            redisMQTemplate.redisTemplate().opsForStream().acknowledge(streamKey, group, recordId);
        }
    }

    /**
     * 改变消费者
     *
     * @param oldConsumerTransferMessageMap 旧消费者待转移消息Map
     */
    protected void changeConsumer(Map<String, List<RecordId>> oldConsumerTransferMessageMap) {
        oldConsumerTransferMessageMap.forEach((oldConsumer, recordIds) -> {
            // 根据当前consumer去获取另外一个consumer
            StreamInfo.XInfoConsumers consumers = redisMQTemplate.redisTemplate().opsForStream().consumers(streamKey, group);
            List<StreamInfo.XInfoConsumer> newConsumers = consumers.stream().filter(consumer -> !consumer.consumerName().equals(oldConsumer)).toList();
            newConsumers.forEach(consumer -> {
                List<MapRecord<String, Object, Object>> mapRecords = redisMQTemplate.redisTemplate().opsForStream().claim(streamKey, group, oldConsumer, RedisStreamCommands.XClaimOptions.minIdle(Duration.ofSeconds(10)).ids(recordIds));
                if (log.isDebugEnabled()) {
                    mapRecords.forEach(entries -> log.debug("转移消息 id: {}, value: {}", entries.getId(), entries.getValue()));
                }
            });
        });
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
