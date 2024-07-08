package org.sunshine.core.cache.config;

import com.alibaba.fastjson2.support.spring6.data.redis.FastJsonRedisSerializer;
import com.alibaba.ttl.TtlRunnable;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
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
import org.sunshine.core.cache.stream.AbstractStreamListener;
import org.sunshine.core.tool.util.INetUtils;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Teamo
 * @since 2023/5/26
 */
@AutoConfiguration(after = CacheAutoConfiguration.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedisStreamAutoConfiguration {

    private final RedisProperties redisProperties;

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisStreamAutoConfiguration(RedisProperties redisProperties,
                                        RedisConnectionFactory redisConnectionFactory) {
        this.redisProperties = redisProperties;
        this.redisConnectionFactory = redisConnectionFactory;
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
    @Bean(initMethod = "start", destroyMethod = "stop")
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer(List<AbstractStreamListener<?>> listeners,
                                                                                                               RedisMQTemplate redisMQTemplate) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        // 批量抓取消息
                        .batchSize(10)
                        .keySerializer(RedisSerializer.string())
                        .hashKeySerializer(RedisSerializer.string())
                        .hashValueSerializer(new FastJsonRedisSerializer<>(String.class))
                        .objectMapper(new ObjectHashMapper())
                        .targetType(String.class)
                        .executor(getExecutor())
                        .build();

        Assert.isTrue(options.getPollTimeout().compareTo(redisProperties.getTimeout()) < 0, "Poll timeout must be smaller than 'spring.redis.timeout'!");

        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container = StreamMessageListenerContainer
                .create(redisConnectionFactory, options);

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

    /**
     * 构建消费者名称
     *
     * @return 本机IP@PID
     */
    private static String buildConsumerName() {
        long currentPID = Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        return String.format("%s@%d", INetUtils.getHostIp(), currentPID);
    }

    /**
     * 获取线程池
     *
     * @return 线程池
     */
    private ThreadPoolTaskExecutor getExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池数量
        executor.setCorePoolSize(corePoolSize);
        //最大线程数量
        executor.setMaxPoolSize(Math.max(corePoolSize * 4, 512));
        //线程池的队列容量
        executor.setQueueCapacity(500);
        //当线程超过corePoolSize，线程存活时间
        executor.setKeepAliveSeconds(60);
        //用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁
        executor.setAwaitTerminationSeconds(120);
        //线程名称的前缀
        executor.setThreadNamePrefix("redis-stream-executor-");
        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 解决子线程无法获取父线程的上下文数据
        executor.setTaskDecorator(TtlRunnable::get);
        executor.initialize();
        return executor;
    }
}
