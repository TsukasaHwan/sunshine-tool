package org.sunshine.core.cache.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.sunshine.core.cache.properties.RedissonProperties;
import org.sunshine.core.cache.redisson.RedissonLocker;
import org.sunshine.core.cache.redisson.codec.FastJsonCodec;
import org.sunshine.core.cache.redisson.util.RedissonLockUtils;
import org.sunshine.core.tool.util.StringUtils;

/**
 * @author Teamo
 * @since 2023/3/27
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "spring.redis", name = "redisson.enable", havingValue = "true")
@EnableConfigurationProperties({RedisProperties.class, RedissonProperties.class})
public class RedissonAutoConfiguration {
    private final RedisProperties redisProperties;

    public RedissonAutoConfiguration(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    /**
     * RedissonClient 单机模式
     *
     * @return RedissonClient
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redisson() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());
        singleServerConfig.setTimeout((int) redisProperties.getTimeout().toMillis());
        singleServerConfig.setDatabase(redisProperties.getDatabase());
        String password = redisProperties.getPassword();
        if (StringUtils.isNotBlank(password)) {
            singleServerConfig.setPassword(password);
        }
        Codec codec = new FastJsonCodec();
        config.setCodec(codec);
        return Redisson.create(config);
    }

    @Bean
    public RedissonLocker redissonLocker(RedissonClient redissonClient) {
        RedissonLocker locker = new RedissonLocker(redissonClient);
        RedissonLockUtils.setLocker(locker);
        return locker;
    }
}
