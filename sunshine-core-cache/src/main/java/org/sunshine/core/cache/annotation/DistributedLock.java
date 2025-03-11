package org.sunshine.core.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解，依赖Redisson
 *
 * @author Teamo
 * @since 2020/6/9
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 锁的key。
     */
    String value();

    /**
     * 锁的key。（如果配置了，则锁的键值为value + key）
     * 支持SpEL表达式。
     */
    String key() default "";

    /**
     * 是否使用尝试锁。
     */
    boolean tryLock() default false;

    /**
     * 最长等待时间。
     * 该字段只有当tryLock()返回true才有效。
     */
    long waitTime() default 10L;

    /**
     * 锁超时时间。
     * 超时时间过后，锁自动释放。
     * 建议：
     * 尽量缩简需要加锁的逻辑。
     */
    long leaseTime() default 5L;

    /**
     * 时间单位。默认为秒。
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
