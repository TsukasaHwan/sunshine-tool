package org.sunshine.core.tool.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author Teamo
 * @since 2020/6/11
 */
public class ReflectUtils extends ReflectionUtils {

    /**
     * 查找对象的指定字段名称的属性值
     *
     * @param obj          对象
     * @param propertyName 属性名称
     * @return 属性值
     */
    public static Object getPropertyValue(Object obj, String propertyName) {
        if (Objects.isNull(obj)) {
            return null;
        }
        Class<?> aClazz = obj.getClass();
        Field field = findField(aClazz, propertyName);
        if (Objects.isNull(field)) {
            return null;
        }
        field.setAccessible(true);
        return getField(field, obj);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object obj, String methodName) {
        Method method = findMethod(obj.getClass(), methodName);
        return method == null ? null : (T) invokeMethod(method, obj);
    }
}
