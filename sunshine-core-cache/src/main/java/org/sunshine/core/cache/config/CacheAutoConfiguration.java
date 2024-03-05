package org.sunshine.core.cache.config;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.data.redis.FastJsonRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.sunshine.core.cache.RedisClient;
import org.sunshine.core.cache.RedisClientImpl;
import org.sunshine.core.cache.aspect.DistributedLockAspect;
import org.sunshine.core.cache.aspect.RateLimitAspect;
import org.sunshine.core.cache.support.CustomCacheManager;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2020/9/17
 */
@EnableCaching
@AutoConfiguration
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class CacheAutoConfiguration {

    private final static Logger log = LoggerFactory.getLogger(CacheAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        RedisSerializer<?> redisSerializer = getRedisSerializer(new FastJsonRedisSerializer<>(Object.class));
        // key采用String的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // value序列化方式采用fastjson
        template.setValueSerializer(redisSerializer);
        // hash的value序列化方式采用fastjson
        template.setHashValueSerializer(redisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisClient redisClient(RedisTemplate<String, Object> redisTemplate) {
        return new RedisClientImpl(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(RedisCacheManager.class)
    public RedisCacheManager redisCacheManager(LettuceConnectionFactory factory) {
        RedisSerializer<?> redisSerializer = getRedisSerializer(new FastJsonRedisSerializer<>(Object.class));
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2L))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer));
        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(factory)).cacheDefaults(redisCacheConfiguration).build();
    }

    @Bean
    @ConditionalOnMissingBean(CaffeineCacheManager.class)
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                // 超出时淘汰
                .maximumSize(10000)
                // 写入后经过120秒过期
                .expireAfterWrite(120, TimeUnit.SECONDS);
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    /**
     * 二级缓存与Spring结合
     *
     * @param caffeineCacheManager caffeine缓存管理器
     * @param redisCacheManager    redis缓存管理器
     * @return CacheManager
     */
    @Bean
    @Primary
    public CacheManager cacheManager(CaffeineCacheManager caffeineCacheManager, RedisCacheManager redisCacheManager) {
        return new CustomCacheManager(caffeineCacheManager, redisCacheManager);
    }

    /**
     * 获取对应的redis序列化器
     *
     * @param redisSerializer redis序列化器
     * @return 一个完整配置的redis序列化器
     */
    private RedisSerializer<?> getRedisSerializer(RedisSerializer<?> redisSerializer) {
        Assert.notNull(redisSerializer, "RedisSerializer must not be null!");
        if (redisSerializer instanceof Jackson2JsonRedisSerializer<?>) {
            ObjectMapper om = new ObjectMapper();
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
            om.findAndRegisterModules();
            redisSerializer = new Jackson2JsonRedisSerializer<>(om, Object.class);
        } else if (redisSerializer instanceof FastJsonRedisSerializer<?> fastJsonRedisSerializer) {
            FastJsonConfig fastJsonConfig = fastJsonRedisSerializer.getFastJsonConfig();
            fastJsonConfig.setReaderFeatures(
                    JSONReader.Feature.FieldBased,
                    JSONReader.Feature.SupportArrayToBean,
                    JSONReader.Feature.SupportAutoType
            );
            fastJsonConfig.setWriterFeatures(
                    JSONWriter.Feature.WriteClassName,
                    JSONWriter.Feature.WriteMapNullValue,
                    JSONWriter.Feature.NotWriteNumberClassName
            );
        } else {
            log.warn("Did not find what you need RedisSerializer, please confirm the correctness of the RedisSerializer!");
        }
        return redisSerializer;
    }

    @Bean
    public DistributedLockAspect distributedLockAspect() {
        return new DistributedLockAspect();
    }

    @Bean
    public RateLimitAspect rateLimitAspect(RedisTemplate<String, Object> redisTemplate) {
        return new RateLimitAspect(redisTemplate);
    }
}
