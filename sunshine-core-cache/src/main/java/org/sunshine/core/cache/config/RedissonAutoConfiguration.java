package org.sunshine.core.cache.config;

import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.redission.JSONCodec;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Bean;
import org.sunshine.core.cache.aspect.DistributedLockAspect;
import org.sunshine.core.cache.properties.DataRedissonProperties;
import org.sunshine.core.cache.redisson.DistributedTaskExecutor;
import org.sunshine.core.cache.redisson.RedissonLockTemplate;
import org.sunshine.core.cache.redisson.queue.DelayedQueueListener;
import org.sunshine.core.cache.redisson.queue.DelayedQueueListenerConfigurer;
import org.sunshine.core.cache.redisson.queue.RedissonDelayedMQTemplate;
import org.sunshine.core.cache.redisson.queue.RedissonDelayedMQTemplateImpl;
import org.sunshine.core.tool.util.StringUtils;

import java.util.List;

/**
 * @author Teamo
 * @since 2023/3/27
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "spring.data.redis.redisson", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({DataRedisProperties.class, DataRedissonProperties.class})
public class RedissonAutoConfiguration {

    private static final String REDIS_PROTOCOL_PREFIX = "redis://";

    private final DataRedisProperties dataRedisProperties;

    public RedissonAutoConfiguration(DataRedisProperties dataRedisProperties) {
        this.dataRedisProperties = dataRedisProperties;
    }

    /**
     * RedissonClient 单机模式
     *
     * @return RedissonClient
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @SuppressWarnings("deprecation")
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(REDIS_PROTOCOL_PREFIX + dataRedisProperties.getHost() + ":" + dataRedisProperties.getPort());
        if (dataRedisProperties.getTimeout() != null) {
            singleServerConfig.setTimeout((int) dataRedisProperties.getTimeout().toMillis());
        }
        singleServerConfig.setDatabase(dataRedisProperties.getDatabase());
        String password = dataRedisProperties.getPassword();
        if (StringUtils.isNotBlank(password)) {
            singleServerConfig.setPassword(password);
        }
        JSONWriter.Feature[] writerFeatures = {JSONWriter.Feature.WriteClassName};
        JSONReader.Feature[] readerFeatures = {JSONReader.Feature.SupportAutoType};
        JSONCodec codec = new JSONCodec(
                JSONFactory.createWriteContext(writerFeatures),
                JSONFactory.createReadContext(readerFeatures)
        );
        config.setCodec(codec);
        return Redisson.create(config);
    }

    @Bean
    public RedissonLockTemplate redissonLockTemplate(RedissonClient redissonClient) {
        return new RedissonLockTemplate(redissonClient);
    }

    @Bean
    public RedissonDelayedMQTemplate redissonDelayedMQTemplate(RedissonClient redissonClient) {
        return new RedissonDelayedMQTemplateImpl(redissonClient);
    }

    @Bean
    public DistributedLockAspect distributedLockAspect(RedissonLockTemplate redissonLockTemplate) {
        return new DistributedLockAspect(redissonLockTemplate);
    }

    @Bean(destroyMethod = "destroy")
    @ConditionalOnBean(DelayedQueueListener.class)
    public DelayedQueueListenerConfigurer delayedQueueListenerConfigurer(List<DelayedQueueListener<?>> delayedQueueListenerList, RedissonClient redissonClient) {
        return new DelayedQueueListenerConfigurer(delayedQueueListenerList, redissonClient);
    }

    @Bean
    public DistributedTaskExecutor distributedLockedTaskExecutor(RedissonLockTemplate redissonLockTemplate) {
        return DistributedTaskExecutor.create(redissonLockTemplate);
    }
}
