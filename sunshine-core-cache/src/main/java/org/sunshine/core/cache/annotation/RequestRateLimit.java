package org.sunshine.core.cache.annotation;

import org.sunshine.core.cache.enums.LimitType;
import org.sunshine.core.tool.util.StringPool;

import java.lang.annotation.*;

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
     * 资源名称
     *
     * @return String
     */
    String name() default StringPool.EMPTY;

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
     * 时间周期
     *
     * @return int
     */
    int period();

    /**
     * 请求次数
     *
     * @return int
     */
    int count();

    /**
     * 限制类型
     *
     * @return LimitType
     */
    LimitType limitType() default LimitType.DEFAULT;
}
