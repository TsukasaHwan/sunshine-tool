package org.sunshine.core.cache.config;

import com.alibaba.fastjson2.support.spring6.data.redis.FastJsonRedisSerializer;
import com.alibaba.ttl.TtlRunnable;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.sunshine.core.cache.RedisMQTemplate;
import org.sunshine.core.cache.RedisMQTemplateImpl;
import org.sunshine.core.cache.properties.RedisStreamProperties;
import org.sunshine.core.cache.redisson.support.DistributedTaskExecutor;
import org.sunshine.core.cache.stream.AbstractStreamListener;
import org.sunshine.core.cache.stream.StreamDeadLetterQueueProcessor;
import org.sunshine.core.tool.util.INetUtils;

import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * @author Teamo
 * @since 2023/5/26
 */
@AutoConfiguration(after = {CacheAutoConfiguration.class, RedissonAutoConfiguration.class})
@EnableConfigurationProperties({RedisProperties.class, RedisStreamProperties.class})
public class RedisStreamAutoConfiguration {

    private final RedisProperties redisProperties;
    private final RedisStreamProperties redisStreamProperties;

    public RedisStreamAutoConfiguration(RedisProperties redisProperties,
                                        RedisStreamProperties redisStreamProperties) {
        this.redisProperties = redisProperties;
        this.redisStreamProperties = redisStreamProperties;
    }

    @Bean
    public RedisMQTemplate redisMQTemplate(RedisTemplate<String, Object> redisTemplate) {
        return new RedisMQTemplateImpl(redisTemplate);
    }

    /**
     * 创建Stream流监听容器
     *
     * @param listeners 监听器
     * @return 完整配置的消息监听容器
     */
    @ConditionalOnBean(AbstractStreamListener.class)
    @ConditionalOnMissingBean(StreamMessageListenerContainer.class)
    @Bean(initMethod = "start", destroyMethod = "stop")
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer(List<AbstractStreamListener<?>> listeners,
                                                                                                               RedisMQTemplate redisMQTemplate) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptionsBuilder<String, ObjectRecord<String, String>> optionsBuilder = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .batchSize(redisStreamProperties.getBatchSize())
                .keySerializer(RedisSerializer.string())
                .hashKeySerializer(RedisSerializer.string())
                .hashValueSerializer(new FastJsonRedisSerializer<>(String.class))
                .objectMapper(new ObjectHashMapper())
                .targetType(String.class);
        if (redisStreamProperties.getThreadPool().isEnable()) {
            optionsBuilder.executor(redisStreamThreadPoolExecutor());
        }
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options = optionsBuilder.build();

        Assert.isTrue(options.getPollTimeout().compareTo(redisProperties.getTimeout()) < 0, "Poll timeout must be smaller than 'spring.redis.timeout'!");

        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container = StreamMessageListenerContainer
                .create(redisMQTemplate.redisTemplate().getRequiredConnectionFactory(), options);

        String consumerName = buildConsumerName();
        listeners.parallelStream().forEach(listener -> {
            String streamKey = listener.getStreamKey();
            String group = listener.getGroup();

            try {
                redisMQTemplate.redisTemplate().opsForStream().createGroup(streamKey, group);
            } catch (Exception ignore) {
            }

            listener.setRedisMQTemplate(redisMQTemplate);

            StreamMessageListenerContainer.StreamReadRequest<String> streamReadRequest = StreamMessageListenerContainer.StreamReadRequest
                    // 指定消费最新的消息
                    .builder(StreamOffset.create(streamKey, ReadOffset.lastConsumed()))
                    // 创建消费者
                    .consumer(Consumer.from(group, consumerName))
                    .cancelOnError(e -> false)
                    // 关闭自动ack确认
                    .autoAcknowledge(false)
                    .build();
            // 指定消费者对象
            container.register(streamReadRequest, listener);
        });
        return container;
    }

    @Bean
    @ConditionalOnBean({StreamMessageListenerContainer.class, DistributedTaskExecutor.class})
    public StreamDeadLetterQueueProcessor streamDeadLetterQueueProcessor(List<AbstractStreamListener<?>> listeners,
                                                                         RedisMQTemplate redisMQTemplate,
                                                                         DistributedTaskExecutor distributedTaskExecutor) {
        return new StreamDeadLetterQueueProcessor(listeners, redisMQTemplate, distributedTaskExecutor);
    }

    /**
     * 构建消费者名称
     *
     * @return 本机IP@PID
     */
    private static String buildConsumerName() {
        long currentPID = ProcessHandle.current().pid();
        return String.format("%s@%d", INetUtils.getHostIp(), currentPID);
    }

    /**
     * 配置一个用于Redis Stream操作的线程池执行器
     *
     * @return 配置好的ThreadPoolTaskExecutor实例
     */
    private ThreadPoolTaskExecutor redisStreamThreadPoolExecutor() {
        RedisStreamProperties.ThreadPool threadPool = redisStreamProperties.getThreadPool();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPool.getCorePoolSize());
        executor.setMaxPoolSize(threadPool.getMaxPoolSize());
        executor.setQueueCapacity(threadPool.getQueueCapacity());
        executor.setKeepAliveSeconds(threadPool.getKeepAliveSeconds());
        executor.setWaitForTasksToCompleteOnShutdown(threadPool.getWaitForJobsToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(threadPool.getAwaitTerminationSeconds());
        executor.setThreadNamePrefix(threadPool.getThreadNamePrefix());
        RejectedExecutionHandler rejectedExecutionHandler = BeanUtils.instantiateClass(threadPool.getRejectedExecutionHandler());
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        executor.setTaskDecorator(TtlRunnable::get);
        executor.initialize();
        return executor;
    }
}
