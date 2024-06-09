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
     * 锁的名称。
     * 如果lockName可以确定，直接设置该属性。
     */
    String name() default "";

    /**
     * lockName前缀
     */
    String prefix() default "";

    /**
     * lockName后缀
     */
    String suffix() default "";

    /**
     * 获得锁名时拼接前后缀用到的分隔符
     */
    String separator() default "";

    /**
     * <pre>
     *     获取注解的方法参数列表的某个参数对象的某个属性值来作为lockName。因为有时候lockName是不固定的。
     *     当param不为空时，可以通过argNum参数来设置具体是参数列表的第几个参数，不设置则默认取第一个。
     * </pre>
     */
    String param() default "";

    /**
     * 将方法第argNum个参数作为锁
     */
    int argNum() default 0;

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
