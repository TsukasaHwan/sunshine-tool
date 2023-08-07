package org.sunshine.core.captcha.service;

import com.xingyuv.captcha.service.CaptchaCacheService;
import jakarta.annotation.Resource;
import org.sunshine.core.cache.RedisClient;

/**
 * @author Teamo
 * @since 2023/8/7
 */
public class RedisCaptchaServiceImpl implements CaptchaCacheService {

    @Resource
    private RedisClient redisClient;

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        redisClient.set(key, value, expiresInSeconds);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisClient.hasKey(key));
    }

    @Override
    public void delete(String key) {
        redisClient.del(key);
    }

    @Override
    public String get(String key) {
        return (String) redisClient.get(key).orElse(null);
    }

    @Override
    public String type() {
        return "redis";
    }

    @Override
    public Long increment(String key, long val) {
        return redisClient.incr(key, val);
    }
}
