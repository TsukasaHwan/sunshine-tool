package org.sunshine.enums.conversion.annotation;

import java.lang.annotation.*;

/**
 * @author: Teamo
 * @date: 2020/1/18 10:22
 * @description: json 与对象的转换注解
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface JsonAutoConverter {

    /**
     * 是否开启自动转换
     *
     * @return
     */
    boolean autoApply() default true;

}
