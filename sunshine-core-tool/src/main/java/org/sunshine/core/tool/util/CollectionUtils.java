package org.sunshine.core.tool.util;

import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * 集合工具类
 *
 * @author Teamo
 */
public class CollectionUtils extends org.springframework.util.CollectionUtils {

    /**
     * Check whether the given Array contains the given element.
     *
     * @param array   the Array to check
     * @param element the element to look for
     * @param <T>     The generic tag
     * @return {@code true} if found, {@code false} else
     */
    public static <T> boolean contains(@Nullable T[] array, final T element) {
        if (array == null) {
            return false;
        }
        return Arrays.stream(array).anyMatch(x -> ObjectUtils.nullSafeEquals(x, element));
    }

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        if (null == obj) {
            return false;
        }
        return obj.getClass().isArray();
    }

    /**
     * Determine whether the given Collection is not empty:
     * i.e. {@code null} or of zero length.
     *
     * @param coll the Collection to check
     * @return boolean
     */
    public static boolean isNotEmpty(@Nullable Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * Determine whether the given Map is not empty:
     * i.e. {@code null} or of zero length.
     *
     * @param map the Map to check
     * @return boolean
     */
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !isEmpty(map);
    }

}
