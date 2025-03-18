package org.sunshine.core.cache.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.Assert;
import org.sunshine.core.cache.annotation.RateLimit;
import org.sunshine.core.tool.exception.BusinessException;
import org.sunshine.core.tool.util.StringPool;
import org.sunshine.core.tool.util.StringUtils;
import org.sunshine.core.tool.util.WebUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2021/04/23
 */
@Aspect
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    public RateLimitAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        HttpServletRequest request = WebUtils.getRequest();
        Assert.notNull(request, "HttpServletRequest is null");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method signatureMethod = signature.getMethod();
        String key = rateLimit.key();
        if (StringUtils.isBlank(key)) {
            switch (rateLimit.keyType()) {
                case METHOD -> key = signatureMethod.getName();
                case IP -> key = WebUtils.getIP();
                default -> throw new IllegalArgumentException("Invalid rate limit key type: " + rateLimit.keyType());
            }
        }
        List<String> keys = Collections.singletonList(rateLimit.prefix() + key + StringPool.COLON + request.getRequestURI());
        Long result = selectLimitType(keys, rateLimit);
        if (result == null || result.equals(0L)) {
            throw new BusinessException(rateLimit.msg());
        }
        return joinPoint.proceed();
    }

    private Long selectLimitType(List<String> keys, RateLimit rateLimit) {
        RateLimit.RateLimitType type = rateLimit.type();
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);

        TimeUnit unit = rateLimit.unit();
        RateLimitScriptSingleton scriptSingleton = RateLimitScriptSingleton.INSTANCE;
        Object[] args;
        switch (type) {
            case FIXED_WINDOW -> {
                // 固定窗口
                redisScript.setScriptSource(scriptSingleton.getScriptSource(RateLimit.RateLimitType.FIXED_WINDOW));
                args = new Object[]{rateLimit.limit(), unit.toSeconds(rateLimit.windowSize())};
            }
            case SLIDING_WINDOW -> {
                // 滑动窗口
                redisScript.setScriptSource(scriptSingleton.getScriptSource(RateLimit.RateLimitType.SLIDING_WINDOW));
                long currentTime = System.currentTimeMillis();
                long windowStart = currentTime - unit.toMillis(rateLimit.windowSize());
                args = new Object[]{currentTime, windowStart, rateLimit.limit()};
            }
            default -> throw new IllegalArgumentException("Invalid rate limit type: " + type);
        }

        return redisTemplate.execute(redisScript, keys, args);
    }

    private enum RateLimitScriptSingleton {
        INSTANCE;

        private final Map<RateLimit.RateLimitType, ResourceScriptSource> scriptSources = new EnumMap<>(RateLimit.RateLimitType.class);

        {
            scriptSources.put(RateLimit.RateLimitType.FIXED_WINDOW, new ResourceScriptSource(new ClassPathResource("scripts/fixed_window.lua")));
            scriptSources.put(RateLimit.RateLimitType.SLIDING_WINDOW, new ResourceScriptSource(new ClassPathResource("scripts/sliding_window.lua")));
        }

        public ResourceScriptSource getScriptSource(RateLimit.RateLimitType type) {
            return scriptSources.get(type);
        }
    }
}
