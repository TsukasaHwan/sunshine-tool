package org.sunshine.core.tool.exception;

import org.sunshine.core.tool.api.code.ResultCode;
import org.sunshine.core.tool.api.response.Result;

/**
 * 业务异常类，用于在业务层处理异常时抛出，封装了业务操作的结果信息。
 *
 * @author Teamo
 * @since 2019/7/10
 */
public class BusinessException extends RuntimeException {

    /**
     * 包含业务操作结果的信息，成功或失败的详细信息。
     */
    private final Result<?> result;

    /**
     * 构造函数，用于创建一个包含具体错误结果的业务异常。
     *
     * @param message 错误信息，描述异常的详细信息。
     * @param result  业务操作的结果，封装了操作是否成功及失败的详细信息。
     */
    public BusinessException(String message, Result<?> result) {
        super(message);
        this.result = result;
    }

    /**
     * 构造函数，用于创建一个默认错误结果的业务异常。
     *
     * @param message 错误信息，描述异常的详细信息。
     */
    public BusinessException(String message) {
        this(message, Result.fail(message));
    }

    /**
     * 构造函数，使用结果码初始化业务异常。
     * 适用于已知结果码的情况。
     *
     * @param resultCode 业务操作的结果码。
     */
    public BusinessException(ResultCode resultCode) {
        this(resultCode.msg(), Result.of(resultCode));
    }

    /**
     * 将给定的消息和结果封装成一个业务异常并抛出。
     *
     * @param message 业务异常的消息内容。
     * @param result  相关的结果信息，可以为空。
     */
    public static void throwException(String message, Result<?> result) {
        throw new BusinessException(message, result);
    }

    /**
     * 将给定的消息封装成一个业务异常并抛出。
     *
     * @param message 业务异常的消息内容。
     */
    public static void throwException(String message) {
        throw new BusinessException(message);
    }

    /**
     * 将给定的结果信息封装成一个业务异常并抛出。
     *
     * @param resultCode 相关的结果信息。结果对象中可能包含了需要被抛出的异常的相关信息，例如错误代码或详细错误消息。
     */
    public static void throwException(ResultCode resultCode) {
        throw new BusinessException(resultCode);
    }

    /**
     * 获取业务操作的结果信息。
     *
     * @return Result<?> 返回业务操作的结果，无论成功或失败都会包含相应的信息。
     */
    public Result<?> getResult() {
        return this.result;
    }
}