package org.sunshine.enums.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * 通过code获取枚举值
     *
     * @param enumType   默认的枚举查询转换方法
     * @param code       枚举标识
     * @param <EnumType> 枚举类型
     * @return 枚举
     */
    static <EnumType extends CodeEnum> Optional<EnumType> of(Class<EnumType> enumType, Integer code) {
        return Arrays.stream(enumType.getEnumConstants()).filter(ele -> ele.code().equals(code)).findFirst();
    }

    /**
     * 通过value获取枚举值
     *
     * @param enumType   默认的枚举查询转换方法
     * @param value      枚举的值
     * @param <EnumType> 枚举类型
     * @return 枚举列表
     */
    static <EnumType extends CodeEnum> List<EnumType> of(Class<EnumType> enumType, String value) {
        return Arrays.stream(enumType.getEnumConstants()).filter(ele -> ele.value().equals(value)).collect(Collectors.toList());
    }
}
