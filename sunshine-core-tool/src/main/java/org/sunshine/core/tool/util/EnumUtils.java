package org.sunshine.core.tool.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 注：枚举类成员变量必须定义为 value, desc
 *
 * @author Teamo
 * @since 2022/08/18
 */
public class EnumUtils {

    private static final Logger log = LoggerFactory.getLogger(EnumUtils.class);

    private static final String VALUE_FIELD_NAME = "value";

    private static final String DESC_FIELD_NAME = "desc";

    private static final String GET_VALUE_METHOD_NAME = "get" + StringUtils.upperFirst(VALUE_FIELD_NAME);

    private static final String GET_DESC_METHOD_NAME = "get" + StringUtils.upperFirst(DESC_FIELD_NAME);

    /**
     * value转换为desc
     *
     * @param value value
     * @param clazz 枚举class
     * @return desc
     */
    public static String convert(int value, Class<?> clazz) {
        if (!clazz.isEnum()) {
            return StringPool.EMPTY;
        }
        Object[] enumConstants = clazz.getEnumConstants();

        Field field = ReflectUtils.findField(clazz, VALUE_FIELD_NAME);
        if (Objects.isNull(field)) {
            return StringPool.EMPTY;
        }
        field.setAccessible(true);

        return Arrays.stream(enumConstants)
                .filter(Try.test(enumObj -> Objects.equals(field.getInt(enumObj), value), ex -> log.error(ex.getMessage(), ex)))
                .map(Try.apply(enumObj -> {
                    Method method = clazz.getMethod(GET_DESC_METHOD_NAME);
                    return method.invoke(enumObj).toString();
                }))
                .findFirst()
                .orElse(StringPool.EMPTY);
    }

    /**
     * desc转换为value
     *
     * @param desc  desc
     * @param clazz 枚举class
     * @return value
     */
    public static Integer convert(String desc, Class<?> clazz) {
        if (!clazz.isEnum()) {
            return null;
        }
        Object[] enumConstants = clazz.getEnumConstants();

        Field field = ReflectUtils.findField(clazz, DESC_FIELD_NAME);
        if (Objects.isNull(field)) {
            return null;
        }
        field.setAccessible(true);

        return Arrays.stream(enumConstants)
                .filter(Try.test(enumObj -> Objects.equals(field.get(enumObj), desc), ex -> log.error(ex.getMessage(), ex)))
                .map(Try.apply(enumObj -> {
                    Method method = clazz.getMethod(GET_VALUE_METHOD_NAME);
                    return Integer.valueOf(method.invoke(enumObj).toString());
                }))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据value, class查找指定的枚举
     *
     * @param value value
     * @param clazz 枚举class
     * @param <T>   T
     * @return 枚举Optional的包装
     */
    public static <T> Optional<T> of(int value, Class<T> clazz) {
        if (!clazz.isEnum()) {
            return Optional.empty();
        }
        T[] enumConstants = clazz.getEnumConstants();

        Field field = ReflectUtils.findField(clazz, VALUE_FIELD_NAME);
        if (Objects.isNull(field)) {
            return Optional.empty();
        }
        field.setAccessible(true);

        return Arrays.stream(enumConstants).filter(Try.test(t -> field.getInt(t) == value)).findFirst();
    }
}
