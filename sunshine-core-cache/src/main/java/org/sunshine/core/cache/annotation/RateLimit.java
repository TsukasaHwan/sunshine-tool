package org.sunshine.core.cache.annotation;

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
public @interface RateLimit {

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
     * 时间窗口大小
     *
     * @return long
     */
    long windowSize();

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
     * 限流类型
     *
     * @return {@link RateLimitType}
     */
    RateLimitType type();

    /**
     * 限制key类型
     *
     * @return {@link RateLimitKeyType}
     */
    RateLimitKeyType keyType();

    enum RateLimitType {

        /**
         * 固定窗口
         * 在固定时间段内允许要求的请求数量访问，超过则拒绝
         */
        FIXED_WINDOW,

        /**
         * 滑动窗口
         * 在一定的时间段内允许要求的请求数量访问，超过则拒绝
         */
        SLIDING_WINDOW
    }

    enum RateLimitKeyType {

        /**
         * 基于方法限流
         */
        METHOD,

        /**
         * 基于IP限流
         */
        IP
    }
}
