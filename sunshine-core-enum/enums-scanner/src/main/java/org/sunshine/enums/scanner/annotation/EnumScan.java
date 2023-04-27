package org.sunshine.enums.scanner.annotation;

import java.lang.annotation.*;

/**
 * @author: Teamo
 * @date: 2019-07-25 10:34
 * @description: 标记需要被扫描的枚举码表, 以及其默认属性
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EnumScan {

    /**
     * 默认的枚举值
     *
     * @return 默认的枚举值
     */
    int defaultEnumCode() default 1;

}
