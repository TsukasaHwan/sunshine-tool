import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Teamo
 * @since 2022/02/24
 */
public class Ognl {

    /**
     * 可以用于判断Optional,CharSequence,Map,Collection,Array是否为空
     *
     * @param o 对象
     * @return 是否为空
     */
    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof Optional) {
            return !((Optional<?>) o).isPresent();
        }
        if (o instanceof CharSequence) {
            return ((CharSequence) o).length() == 0;
        }
        if (o.getClass().isArray()) {
            return Array.getLength(o) == 0;
        }
        if (o instanceof Collection) {
            return ((Collection<?>) o).isEmpty();
        }
        if (o instanceof Map) {
            return ((Map<?, ?>) o).isEmpty();
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
