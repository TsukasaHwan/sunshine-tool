package org.sunshine.enums.conversion.converter;

import jakarta.persistence.AttributeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author: Teamo (Updated for Spring Boot 4.0 / Jackson 3)
 * @date: 2024-05-22
 * @description: 通用型 实体类jsonBean <--> json 字段 转换基类
 * 使用方式 :
 * 1. 在实体类的字段上面 @Convert(Converter=子类.class)
 * 2. 在子类上加入 @Converter(autoApply = true), 可开启jpa全局自动转换
 */
public abstract class AbstractJsonConverter<T> implements AttributeConverter<T, String> {

    protected static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        AbstractJsonConverter.objectMapper = objectMapper.rebuild()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        ObjectWriter writer = objectMapper.writerFor(getJsonType());
        return writer.writeValueAsString(attribute);
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        ObjectReader reader = objectMapper.readerFor(getJsonType());
        if (dbData == null) {
            return null;
        }
        return reader.readValue(dbData);
    }

    /**
     * 获取子类中 json 转换的 TypeReference
     *
     * @return TypeReference<T>
     */
    protected TypeReference<T> getJsonType() {
        Type[] actualTypeArguments = ((ParameterizedType) (getClass().getGenericSuperclass())).getActualTypeArguments();
        if (actualTypeArguments.length > 0) {
            return new TypeReference<>() {
                @Override
                public Type getType() {
                    return actualTypeArguments[0];
                }
            };
        }
        return null;
    }
}
