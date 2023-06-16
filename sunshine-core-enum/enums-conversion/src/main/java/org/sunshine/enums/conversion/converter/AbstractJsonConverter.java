package org.sunshine.enums.conversion.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.AttributeConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author: Teamo
 * @date: 2019-07-17 16:17
 * @description: 通用型 实体类jsonBean <--> json 字段 转换基类
 * 使用方式 :
 * 1. 在实体类的字段上面 @Convert(Converter=子类.class)
 * 2. 在子类上加入 @Converter(autoApply = true), 可开启jpa全局自动转换
 */
public abstract class AbstractJsonConverter<T> implements AttributeConverter<T, String> {

    protected static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        AbstractJsonConverter.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        objectMapper.setConfig(objectMapper.getSerializationConfig().without(SerializationFeature.FAIL_ON_EMPTY_BEANS));
        ObjectWriter writer = objectMapper.writerFor(getJsonType());
        try {
            return writer.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        ObjectReader reader = objectMapper.readerFor(getJsonType());
        try {
            // null 直接返回,不然会异常
            return dbData == null ? null : reader.readValue(dbData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取子类中 json 转换的 TypeReference
     *
     * @return TypeReference<T>
     */
    protected TypeReference<T> getJsonType() {
        Type[] actualTypeArguments = ((ParameterizedType) (getClass().getGenericSuperclass())).getActualTypeArguments();
        if (actualTypeArguments != null && actualTypeArguments.length > 0) {
            return new TypeReference<T>() {
                @Override
                public Type getType() {
                    return actualTypeArguments[0];
                }

            };
        }

        return null;
    }
}
