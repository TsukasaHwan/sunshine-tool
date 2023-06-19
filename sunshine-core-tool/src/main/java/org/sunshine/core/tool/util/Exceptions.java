package org.sunshine.core.tool.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 异常处理工具类
 *
 * @author L.cm
 */
public class Exceptions {

    /**
     * 将CheckedException转换为UncheckedException.
     *
     * @param e Throwable
     * @return {RuntimeException}
     */
    public static RuntimeException unchecked(Throwable e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
            || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException exception) {
            return new RuntimeException(exception.getTargetException());
        } else if (e instanceof RuntimeException exception) {
            return exception;
        } else {
            return new RuntimeException(e);
        }
    }

    /**
     * 代理异常解包
     *
     * @param wrapped 包装过得异常
     * @return 解包后的异常
     */
    public static Throwable unwrap(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException ex) {
                unwrapped = ex.getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException ex) {
                unwrapped = ex.getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }

}
