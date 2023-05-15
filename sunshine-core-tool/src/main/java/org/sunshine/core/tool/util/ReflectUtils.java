package org.sunshine.core.tool.util;

import org.springframework.util.Assert;
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

    /**
     * 执行对象中指定方法
     *
     * @param <T>        返回对象类型
     * @param obj        方法所在对象
     * @param methodName 方法名
     * @param args       参数列表
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object obj, String methodName, Object... args) {
        Assert.notNull(obj, "Object to get method must be not null!");
        Assert.notNull(methodName, "Method name must be not blank!");
        Method method = findMethod(obj.getClass(), methodName);
        if (method == null) {
            throw new NullPointerException("No such method: [" + methodName + "] from [" + obj.getClass() + "]");
        }
        return (T) invokeMethod(method, obj, args);
    }
}
