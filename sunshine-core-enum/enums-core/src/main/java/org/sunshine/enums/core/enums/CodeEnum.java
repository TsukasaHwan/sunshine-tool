package org.sunshine.enums.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author: Teamo
 * @date: 2020/1/13 13:43
 * @description: 枚举需要实现的接口, 用来获取 code 和 name 的规范
 */
public interface CodeEnum {

    /**
     * 枚举的 code 值
     *
     * @return 枚举标识
     */
    @JsonValue
    Integer code();

    /**
     * 枚举的属性
     *
     * @return 枚举的值
     */
    String value();

    /**
     * 通过code 获取枚举值
     *
     * @param enumType   默认的枚举查询转换方法
     * @param i          枚举标识
     * @param <EnumType> 枚举类型
     * @return 枚举
     */
    static <EnumType extends CodeEnum> EnumType valueOf(Class<EnumType> enumType, Integer i) {
        for (EnumType ele : enumType.getEnumConstants()) {
            if (ele.code().equals(i)) {
                return ele;
            }
        }
        // 检索不到返回null值
        return null;
    }

}
