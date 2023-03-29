package org.sunshine.core.tool.util;

/**
 * @author Teamo
 * @since 2023/01/11
 */
public class ObjectUtils extends org.springframework.util.ObjectUtils {
    /**
     * 判断元素不为空
     *
     * @param obj object
     * @return boolean
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 对象组中是否存在 Empty Object
     *
     * @param os 对象组
     * @return boolean
     */
    public static boolean hasEmpty(final Object... os) {
        if (isEmpty(os)) {
            return true;
        }

        for (final Object o : os) {
            if (isEmpty(o)) {
                return true;
            }
        }
        return false;
    }
}
