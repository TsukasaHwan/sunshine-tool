package org.sunshine.enums.conversion.annotation;

import java.lang.annotation.*;

/**
 * @author: Teamo
 * @date: 2020/1/18 10:20
 * @description: 枚举自动转换注解
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface EnumAutoConverter {

    /**
     * 转换的对应类型
     *
     * @return
     */
    Class<? extends Number> convertType() default Integer.class;

    /**
     * 是否开启自动转换
     *
     * @return
     */
    boolean autoApply() default true;
}
