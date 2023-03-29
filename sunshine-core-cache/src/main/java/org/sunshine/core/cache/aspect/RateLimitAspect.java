package org.sunshine.core.cache.aspect;

import com.google.common.collect.ImmutableList;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.Assert;
import org.sunshine.core.cache.annotation.RateLimit;
import org.sunshine.core.cache.enums.LimitType;
import org.sunshine.core.tool.util.StringPool;
import org.sunshine.core.tool.util.StringUtils;
import org.sunshine.core.tool.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author Teamo
 * @since 2021/04/23
 */
@Aspect
public class RateLimitAspect {

    private final static Logger log = LoggerFactory.getLogger(RateLimitAspect.class);

    private final static ResourceScriptSource RATE_LIMIT_SCRIPT = new ResourceScriptSource(new ClassPathResource("scripts/rate_limit_lua.lua"));

    private final RedisTemplate<String, Object> redisTemplate;

    private final RuntimeException throwException;

    public RateLimitAspect(RedisTemplate<String, Object> redisTemplate, RuntimeException throwException) {
        this.redisTemplate = redisTemplate;
        this.throwException = throwException;
    }

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        HttpServletRequest request = WebUtils.getRequest();
        Assert.notNull(request, "HttpServletRequest is null");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method signatureMethod = signature.getMethod();
        String key = rateLimit.key();
        LimitType limitType = rateLimit.limitType();
        if (StringUtils.isBlank(key)) {
            if (limitType == LimitType.DEFAULT) {
                // 默认取方法名为key
                key = signatureMethod.getName();
            } else if (limitType == LimitType.IP) {
                // 取ip为key
                key = WebUtils.getIP();
            }
        }
        ImmutableList<String> keys = ImmutableList.of(rateLimit.prefix() + key + StringPool.COLON + request.getRequestURI());
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(RATE_LIMIT_SCRIPT);
        Long result = redisTemplate.execute(redisScript, keys, rateLimit.count(), rateLimit.period());
        if (result == null || result.equals(0L)) {
            throw throwException;
        }
        log.info("限流key为 [{}]，描述为 [{}] 的接口", keys, rateLimit.name());
        return joinPoint.proceed();
    }
}
