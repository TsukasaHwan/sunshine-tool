package org.sunshine.core.cache.annotation;

import org.sunshine.core.cache.enums.LimitKeyType;
import org.sunshine.core.cache.enums.LimitType;
import org.sunshine.core.tool.util.StringPool;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 *
 * @author Teamo
 * @since 2021/04/16
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestRateLimit {

    /**
     * key前缀
     *
     * @return String
     */
    String prefix() default StringPool.EMPTY;

    /**
     * 资源key
     *
     * @return String
     */
    String key() default StringPool.EMPTY;

    /**
     * 请求数
     *
     * @return int
     */
    int limit();

    /**
     * 过期时间
     *
     * @return long
     */
    long expire();

    /**
     * 时间单位
     *
     * @return {@link TimeUnit}
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 提示消息
     *
     * @return String
     */
    String msg() default "请勿频繁操作";

    /**
     * 限制key类型
     *
     * @return {@link LimitKeyType}
     */
    LimitKeyType limitKeyType() default LimitKeyType.METHOD;

    /**
     * 限流类型
     *
     * @return {@link LimitType}
     */
    LimitType limitType() default LimitType.FIXED_WINDOW;
}
