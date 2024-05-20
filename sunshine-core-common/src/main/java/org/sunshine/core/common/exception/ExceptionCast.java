package org.sunshine.core.common.exception;

import org.sunshine.core.tool.api.response.Result;
import org.sunshine.core.tool.exception.BusinessException;

/**
 * 提供将特定异常转换为业务异常的工具方法。
 *
 * @author Teamo
 * @since 2019/7/10
 */
public class ExceptionCast {

    /**
     * 将给定的消息和结果封装成一个业务异常并抛出。
     *
     * @param message 业务异常的消息内容。
     * @param result  相关的结果信息，可以为空。
     */
    public static void cast(String message, Result<?> result) {
        throw new BusinessException(message, result);
    }

    /**
     * 将给定的消息封装成一个业务异常并抛出。
     *
     * @param message 业务异常的消息内容。
     */
    public static void cast(String message) {
        throw new BusinessException(message);
    }

    /**
     * 将给定的结果信息封装成一个业务异常并抛出。
     *
     * @param result 相关的结果信息。结果对象中可能包含了需要被抛出的异常的相关信息，例如错误代码或详细错误消息。
     */
    public static void cast(Result<?> result) {
        throw new BusinessException(result);
    }
}
