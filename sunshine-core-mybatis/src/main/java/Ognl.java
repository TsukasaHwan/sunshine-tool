import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Ognl工具类，主要是为了在ognl表达式访问静态方法时可以减少长长的类名称编写
 * Ognl访问静态方法的表达式： @class@method(args)
 *
 * <pre>
 *   &lt;if test="@Ognl@isNotEmpty(userId)">
 *  	and user_id = #{userId}
 *   &lt;/if>
 * </pre>
 *
 * @author Teamo
 * @since 2022/02/24
 */
public class Ognl {

    /**
     * 可以用于判断Optional,CharSequence,Map,Collection,Array是否为空
     *
     * @param obj 对象
     * @return 是否为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof Optional<?> optional) {
            return optional.isEmpty();
        }
        if (obj instanceof CharSequence charSequence) {
            return charSequence.length() == 0;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Collection<?> collection) {
            return collection.isEmpty();
        }
        if (obj instanceof Map<?, ?> map) {
            return map.isEmpty();
        }
        return false;
    }

    /**
     * 判断是否为null
     *
     * @param o 对象
     * @return 是否为null
     */
    public static boolean isNull(Object o) {
        return Objects.isNull(o);
    }

    /**
     * 判断对象是否不为null
     *
     * @param o 对象
     * @return 是否不为null
     */
    public static boolean nonNull(Object o) {
        return Objects.nonNull(o);
    }

    /**
     * 可以用于判断Optional,CharSequence,Map,Collection,Array是否不为空
     *
     * @param o 对象
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    /**
     * 判断对象是否不为空
     *
     * @param o 对象
     * @return 是否不为空
     */
    public static boolean isNotBlank(Object o) {
        return !isBlank(o);
    }

    /**
     * 判断对象是否为数字
     *
     * @param o 对象
     * @return 是否为数字
     */
    public static boolean isNumber(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Number) {
            return true;
        }
        if (o instanceof String) {
            String str = (String) o;
            if (str.length() == 0) {
                return false;
            }
            if (str.trim().length() == 0) {
                return false;
            }
            try {
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 对象是否为空
     *
     * @param o 对象
     * @return 是否为空
     */
    public static boolean isBlank(Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof String) {
            String str = (String) o;
            return isBlank(str);
        }
        return false;
    }

    /**
     * 校验空白字符
     *
     * @param str 字符串
     * @return 是否为空字符串
     */
    public static boolean isBlank(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
