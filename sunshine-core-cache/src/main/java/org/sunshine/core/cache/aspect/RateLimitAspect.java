package org.sunshine.core.cache.aspect;

import com.google.common.collect.ImmutableList;
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

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2021/04/23
 */
@Aspect
public class RateLimitAspect {

    private final static ResourceScriptSource FIXED_WINDOW_LUA = new ResourceScriptSource(new ClassPathResource("scripts/fixed_window.lua"));

    private final static ResourceScriptSource SLIDING_WINDOW_LUA = new ResourceScriptSource(new ClassPathResource("scripts/sliding_window.lua"));

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
            RateLimit.RateLimitKeyType keyType = rateLimit.keyType();
            if (keyType.equals(RateLimit.RateLimitKeyType.METHOD)) {
                // 取方法名为key
                key = signatureMethod.getName();
            } else if (keyType.equals(RateLimit.RateLimitKeyType.IP)) {
                // 取ip为key
                key = WebUtils.getIP();
            }
        }
        ImmutableList<String> keys = ImmutableList.of(rateLimit.prefix() + key + StringPool.COLON + request.getRequestURI());
        Long result = selectLimitType(keys, rateLimit);
        if (result == null || result.equals(0L)) {
            throw new BusinessException(rateLimit.msg());
        }
        return joinPoint.proceed();
    }

    private Long selectLimitType(ImmutableList<String> keys, RateLimit rateLimit) {
        Long result = null;
        RateLimit.RateLimitType type = rateLimit.type();
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        TimeUnit unit = rateLimit.unit();
        redisScript.setResultType(Long.class);
        if (type.equals(RateLimit.RateLimitType.FIXED_WINDOW)) {
            // 固定窗口
            redisScript.setScriptSource(FIXED_WINDOW_LUA);
            result = redisTemplate.execute(redisScript, keys, rateLimit.limit(), unit.toSeconds(rateLimit.windowSize()));
        } else if (type.equals(RateLimit.RateLimitType.SLIDING_WINDOW)) {
            // 滑动窗口
            long currentTime = System.currentTimeMillis();
            long windowStart = currentTime - unit.toMillis(rateLimit.windowSize());
            redisScript.setScriptSource(SLIDING_WINDOW_LUA);
            result = redisTemplate.execute(redisScript, keys, currentTime, windowStart, rateLimit.limit());
        } else {
            throw new IllegalArgumentException("Invalid rate limit type: " + type);
        }
        return result;
    }
}
