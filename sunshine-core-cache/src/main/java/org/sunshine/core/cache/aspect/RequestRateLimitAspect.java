package org.sunshine.core.cache.aspect;

import com.google.common.collect.ImmutableList;
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
import org.sunshine.core.cache.annotation.RequestRateLimit;
import org.sunshine.core.cache.enums.LimitKeyType;
import org.sunshine.core.cache.enums.LimitType;
import org.sunshine.core.tool.exception.CustomException;
import org.sunshine.core.tool.util.StringPool;
import org.sunshine.core.tool.util.StringUtils;
import org.sunshine.core.tool.util.WebUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2021/04/23
 */
@Aspect
public class RequestRateLimitAspect {

    private final static ResourceScriptSource FIX_RATE_LIMIT_SCRIPT = new ResourceScriptSource(new ClassPathResource("scripts/fix_window_limit.lua"));

    private final static ResourceScriptSource SLIDE_RATE_LIMIT_SCRIPT = new ResourceScriptSource(new ClassPathResource("scripts/slide_window_limit.lua"));

    private final RedisTemplate<String, Object> redisTemplate;

    public RequestRateLimitAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(requestRateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RequestRateLimit requestRateLimit) throws Throwable {
        HttpServletRequest request = WebUtils.getRequest();
        Assert.notNull(request, "HttpServletRequest is null");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method signatureMethod = signature.getMethod();
        String key = requestRateLimit.key();
        if (StringUtils.isBlank(key)) {
            LimitKeyType limitKeyType = requestRateLimit.limitKeyType();
            if (limitKeyType.equals(LimitKeyType.METHOD)) {
                // 取方法名为key
                key = signatureMethod.getName();
            } else if (limitKeyType.equals(LimitKeyType.IP)) {
                // 取ip为key
                key = WebUtils.getIP();
            }
        }
        ImmutableList<String> keys = ImmutableList.of(requestRateLimit.prefix() + key + StringPool.COLON + request.getRequestURI());
        Long result = selectLimitType(keys, requestRateLimit);
        if (result == null || result.equals(0L)) {
            throw new CustomException(requestRateLimit.msg());
        }
        return joinPoint.proceed();
    }

    private Long selectLimitType(ImmutableList<String> keys, RequestRateLimit requestRateLimit) {
        Long result = null;
        LimitType limitType = requestRateLimit.limitType();
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        TimeUnit unit = requestRateLimit.unit();
        redisScript.setResultType(Long.class);
        if (limitType.equals(LimitType.FIXED_WINDOW)) {
            // 固定窗口
            redisScript.setScriptSource(FIX_RATE_LIMIT_SCRIPT);
            result = redisTemplate.execute(redisScript, keys, requestRateLimit.limit(), unit.toSeconds(requestRateLimit.expire()));
        } else if (limitType.equals(LimitType.SLIDE_WINDOW)) {
            // 滑动窗口
            long score = System.currentTimeMillis();
            long windowTime = score - unit.toMillis(requestRateLimit.expire());
            redisScript.setScriptSource(SLIDE_RATE_LIMIT_SCRIPT);
            result = redisTemplate.execute(redisScript, keys, windowTime, score, requestRateLimit.limit(), score);
        }
        return result;
    }
}
