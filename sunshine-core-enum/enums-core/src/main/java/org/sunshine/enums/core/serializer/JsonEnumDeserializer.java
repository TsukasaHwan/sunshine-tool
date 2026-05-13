package org.sunshine.enums.core.serializer;

import org.springframework.util.StringUtils;
import org.sunshine.enums.core.enums.CodeEnum;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author Teamo
 * @since 2025-05-13
 */
public class JsonEnumDeserializer extends ValueDeserializer<Enum<?>> {

    private static final Pattern PATTERN = Pattern.compile("^[-+]?[\\d]*$");

    /**
     * 枚举类的class
     */
    @SuppressWarnings("rawtypes")
    private Class clazz;

    /**
     * 执行反序列化
     *
     * @param p    Json解析器
     * @param ctxt 反序列化上下文
     * @return 反序列化后的枚举值
     * @throws JacksonException 当反序列化失败时抛出
     */
    @Override
    @SuppressWarnings("unchecked")
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        if (StringUtils.hasText(p.getString()) && CodeEnum.class.isAssignableFrom(clazz) && isInteger(p.getString())) {
            Optional<Enum<?>> optional = CodeEnum.of(clazz, Integer.valueOf(p.getString()));
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    /**
     * 获取需要转的枚举的 class
     *
     * @param context  反序列化上下文
     * @param property Bean属性
     * @return 一个新实例，用于处理当前属性的反序列化任务
     */
    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        Class<?> rawCls = context.getContextualType().getRawClass();
        JsonEnumDeserializer clone = new JsonEnumDeserializer();
        clone.setClazz(rawCls);
        return clone;
    }

    /**
     * 判断是否为整数
     *
     * @param str 传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    private static boolean isInteger(String str) {
        return PATTERN.matcher(str).matches();
    }

    @SuppressWarnings("rawtypes")
    public Class getClazz() {
        return clazz;
    }

    @SuppressWarnings("rawtypes")
    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
