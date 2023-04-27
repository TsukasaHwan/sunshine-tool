package org.sunshine.enums.core.enums;

/**
 * @author sneb
 * @description 扩展 CodeEnum 方便使用lombok
 * @since 2022-09-22 17:24
 **/
public interface ExtensionCodeEnum extends CodeEnum {

    default Integer code() {
        return getCode();
    }

    /**
     * 枚举的属性
     *
     * @return 枚举的值
     */
    default String value() {
        return getName();
    }

    /**
     * 获取code值
     *
     * @return code
     */
    Integer getCode();

    /**
     * 获取value值
     *
     * @return name
     */
    String getName();
}
