package org.sunshine.core.common.exception;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.sunshine.core.tool.api.code.CommonCode;
import org.sunshine.core.tool.api.code.ResultCode;
import org.sunshine.core.tool.api.response.Result;
import org.sunshine.core.tool.exception.CustomException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 统一异常处理类
 *
 * @author Teamo
 * @since 2019/7/10
 */
@RestControllerAdvice
public class ResponseExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(ResponseExceptionHandler.class);

    /**
     * 线程安全并且不能改变
     */
    private static Map<Class<? extends Throwable>, ResultCode> exceptions;

    private static final ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> BUILDER = ImmutableMap.builder();

    static {
        BUILDER.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
        BUILDER.put(HttpRequestMethodNotSupportedException.class, CommonCode.REQUEST_NOT_SUPPORTED);
        BUILDER.put(AccessDeniedException.class, CommonCode.UNAUTHORIZED);
    }

    /**
     * 已知、未知异常捕获
     *
     * @param ex 异常
     * @return {Result}
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception ex) {
        if (ex instanceof CustomException) {
            return handleCustomException((CustomException) ex);
        } else if (ex instanceof BindException) {
            return handleBindException((BindException) ex);
        } else if (ex instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) ex);
        } else {
            return handleUnknownException(ex);
        }
    }

    /**
     * 自定义异常捕获
     *
     * @param ex 自定义异常
     * @return {Result}
     */
    private Result<?> handleCustomException(CustomException ex) {
        return ex.getResult();
    }

    /**
     * 处理绑定异常
     *
     * @param ex 绑定异常
     * @return {Result}
     */
    private Result<Void> handleBindException(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        if (Objects.nonNull(fieldError)) {
            return Result.fail(fieldError.getDefaultMessage());
        }
        String msg = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(CommonCode.FAIL.msg());
        return Result.fail(msg);
    }

    /**
     * 处理约束违反异常
     *
     * @param ex 约束违反异常
     * @return {Result}
     */
    private Result<Void> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        String msg = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .min(String::compareTo)
                .orElse(CommonCode.FAIL.msg());
        return Result.fail(msg);
    }

    /**
     * 处理未知异常
     *
     * @param ex 异常
     * @return {Result}
     */
    private Result<Void> handleUnknownException(Exception ex) {
        if (exceptions == null) {
            exceptions = BUILDER.build();
        }
        // 从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应系统异常
        if (exceptions.get(ex.getClass()) == null) {
            log.error(ex.getMessage(), ex);
            return Result.fail(CommonCode.SERVER_ERROR);
        }
        return Result.fail(exceptions.get(ex.getClass()));
    }
}
