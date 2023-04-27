package org.sunshine.enums.core.mvc;

import org.sunshine.enums.core.enums.CodeEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sneb
 * @description SpringMVC 枚举数字转换器
 * @since 2020-12-04 16:45
 **/
public class EnumConverterFactory implements ConverterFactory<String, CodeEnum> {

    private static final Map<Class<?>, Converter> converterMap = new HashMap<>();

    @Override
    public <T extends CodeEnum> Converter<String, T> getConverter(Class<T> aClass) {
        Converter<String, T> converter = converterMap.get(aClass);
        if (converter == null) {
            converter = new StringToEnumConverter<>(aClass);
            converterMap.put(aClass, converter);
        }
        return converter;
    }


}

class StringToEnumConverter<T extends CodeEnum> implements Converter<String, T> {

    private Map<String, T> enumMap = new HashMap<>();

    StringToEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(e.code().toString(), e);
        }
    }

    @Override
    public T convert(String source) {

        T t = enumMap.get(source);
        if (t == null) {
            // 异常可以稍后去捕获
            throw new IllegalArgumentException("No element matches " + source);
        }
        return t;
    }
}
