package org.sunshine.core.tool.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Teamo
 * @since 2024/7/8
 */
public class TypeUtils {

    /**
     * 获得给定类的泛型参数
     *
     * @param type  被检查的类型，必须是已经确定泛型类型的类
     * @param index 泛型类型的索引号，即第几个泛型类型
     * @return {@link Type}
     */
    public static Type getTypeArgument(Type type, int index) {
        final Type[] typeArguments = getTypeArguments(type);
        if (null != typeArguments && typeArguments.length > index) {
            return typeArguments[index];
        }
        return null;
    }

    /**
     * 获得指定类型中所有泛型参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到String
     *
     * @param type 指定类型
     * @return 所有泛型参数类型
     */
    public static Type[] getTypeArguments(Type type) {
        if (null == type) {
            return null;
        }
        final ParameterizedType parameterizedType = toParameterizedType(type);
        return (null == parameterizedType) ? null : parameterizedType.getActualTypeArguments();
    }

    /**
     * 将{@link Type} 转换为{@link ParameterizedType}<br>
     * {@link ParameterizedType}用于获取当前类或父类中泛型参数化后的类型<br>
     * 一般用于获取泛型参数具体的参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到B{@link ParameterizedType}，从而获取到String
     *
     * @param type {@link Type}
     * @return {@link ParameterizedType}
     */
    public static ParameterizedType toParameterizedType(final Type type) {
        return toParameterizedType(type, 0);
    }

    /**
     * 将{@link Type} 转换为{@link ParameterizedType}<br>
     * {@link ParameterizedType}用于获取当前类或父类中泛型参数化后的类型<br>
     * 一般用于获取泛型参数具体的参数类型，例如：
     *
     * <pre>{@code
     *   class A<T>
     *   class B extends A<String>;
     * }</pre>
     * <p>
     * 通过此方法，传入B.class即可得到B对应的{@link ParameterizedType}，从而获取到String
     *
     * @param type           {@link Type}
     * @param interfaceIndex 实现的第几个接口
     * @return {@link ParameterizedType}
     */
    public static ParameterizedType toParameterizedType(final Type type, final int interfaceIndex) {
        if (type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }

        if (type instanceof Class) {
            final ParameterizedType[] generics = getGenerics((Class<?>) type);
            if (generics.length > interfaceIndex) {
                return generics[interfaceIndex];
            }
        }

        return null;
    }

    /**
     * 获取指定类所有泛型父类和泛型接口
     *
     * @param clazz 类
     * @return 泛型父类或接口数组
     */
    public static ParameterizedType[] getGenerics(final Class<?> clazz) {
        final List<ParameterizedType> result = new ArrayList<>();
        // 泛型父类（父类及祖类优先级高）
        final Type genericSuper = clazz.getGenericSuperclass();
        if (null != genericSuper && !Object.class.equals(genericSuper)) {
            final ParameterizedType parameterizedType = toParameterizedType(genericSuper);
            if (null != parameterizedType) {
                result.add(parameterizedType);
            }
        }

        // 泛型接口
        final Type[] genericInterfaces = clazz.getGenericInterfaces();
        if (ObjectUtils.isNotEmpty(genericInterfaces)) {
            for (final Type genericInterface : genericInterfaces) {
                final ParameterizedType parameterizedType = toParameterizedType(genericInterface);
                if (null != parameterizedType) {
                    result.add(parameterizedType);
                }
            }
        }
        return result.toArray(new ParameterizedType[0]);
    }

}
