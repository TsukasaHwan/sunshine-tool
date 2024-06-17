package org.sunshine.core.cache.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.sunshine.core.cache.RedisClient;

import java.time.Duration;

/**
 * @author Teamo
 * @since 2023/5/26
 */
@EnableConfigurationProperties(RedisProperties.class)
public abstract class AbstractRedisStreamConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    protected final RedisConnectionFactory redisConnectionFactory;

    protected final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    protected final RedisClient redisClient;

    public AbstractRedisStreamConfiguration(RedisConnectionFactory redisConnectionFactory, ThreadPoolTaskExecutor threadPoolTaskExecutor, RedisClient redisClient) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.redisClient = redisClient;
    }

    /**
     * 通用创建Stream流监听容器方法
     * 不存在Stream时会自动创建并绑定消息
     *
     * @param redisStreamKey {@link RedisStreamKey}
     * @param pollTimeout    没有消息时，阻塞多长时间，需要比spring.redis.timeout的时间小
     * @param batchSize      批量抓取消息数量
     * @param clazz          转换类
     * @param streamListener 监听器
     * @param <T>            泛型 T
     * @return 完整配置的消息监听容器
     */
    protected <T> StreamMessageListenerContainer<String, ObjectRecord<String, T>> applyListenerContainer(RedisStreamKey redisStreamKey,
                                                                                                         Duration pollTimeout,
                                                                                                         int batchSize,
                                                                                                         Class<T> clazz,
                                                                                                         AbstractStreamListener<T> streamListener) {
        Assert.notNull(pollTimeout, "Poll timeout must not be null!");
        Assert.isTrue(pollTimeout.compareTo(redisProperties.getTimeout()) < 0, "Poll timeout must be smaller than 'spring.redis.timeout'!");

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, T>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                // 没有消息时，阻塞多长时间，需要比spring.redis.timeout的时间小
                .pollTimeout(pollTimeout)
                // 批量抓取消息
                .batchSize(batchSize)
                .keySerializer(RedisSerializer.string())
                .hashKeySerializer(RedisSerializer.string())
                .hashValueSerializer(new Jackson2JsonRedisSerializer<>(clazz))
                .objectMapper(new ObjectHashMapper())
                // 传递的数据类型
                .targetType(clazz)
                .executor(threadPoolTaskExecutor)
                .build();
        StreamMessageListenerContainer<String, ObjectRecord<String, T>> container = StreamMessageListenerContainer
                .create(redisConnectionFactory, options);

        StreamMessageListenerContainer.StreamReadRequest<String> streamReadRequest = StreamMessageListenerContainer.StreamReadRequest
                // 指定消费最新的消息
                .builder(StreamOffset.create(redisStreamKey.stream(), ReadOffset.lastConsumed()))
                // 创建消费者
                .consumer(Consumer.from(redisStreamKey.group(), redisStreamKey.consumer()))
                .cancelOnError(e -> false)
                // 关闭自动ack确认
                .autoAcknowledge(false)
                .build();
        // 指定消费者对象
        container.register(streamReadRequest, streamListener);
        // 创建消费组
        Boolean hasKey = redisClient.hasKey(redisStreamKey.stream());
        if (!hasKey) {
            redisClient.streamCreateGroup(redisStreamKey.stream(), redisStreamKey.group());
        }
        return container;
    }
}
