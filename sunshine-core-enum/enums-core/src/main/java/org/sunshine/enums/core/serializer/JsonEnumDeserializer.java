package org.sunshine.enums.core.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.springframework.util.StringUtils;
import org.sunshine.enums.core.enums.CodeEnum;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author: Teamo
 * @date: 18:05
 * @description: 枚举反序列化
 */
public class JsonEnumDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {

    private static final Pattern PATTERN = Pattern.compile("^[-\\+]?[\\d]*$");

    /**
     * 枚举类的class
     */
    private Class clazz;

    /**
     * 执行反序列化
     *
     * @param p
     * @param context
     * @return
     * @throws IOException
     */
    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext context) throws IOException {

        if (StringUtils.hasText(p.getText()) && CodeEnum.class.isAssignableFrom(clazz) && isInteger(p.getText())) {
            Optional optional = CodeEnum.of(clazz, Integer.valueOf(p.getText()));
            if (optional.isPresent()) {
                return (Enum<?>) optional.get();
            }
        }

        return null;
    }

    /**
     * 获取 需要转的枚举的 class
     *
     * @param context
     * @param property
     * @return
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
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
    public static boolean isInteger(String str) {
        return PATTERN.matcher(str).matches();
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
