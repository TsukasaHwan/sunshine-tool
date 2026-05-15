package org.sunshine.core.common.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.sunshine.core.cache.exception.DistributedLockAcquisitionException;
import org.sunshine.core.cache.exception.RateLimitExceededException;
import org.sunshine.core.tool.api.code.CommonCode;
import org.sunshine.core.tool.api.response.Result;
import org.sunshine.core.tool.exception.BusinessException;

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

    private static final Logger log = LoggerFactory.getLogger(ResponseExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException ex) {
        return ex.getResult();
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException ex) {
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

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        String msg = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .min(String::compareTo)
                .orElse(CommonCode.FAIL.msg());
        return Result.fail(msg);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public Result<?> handleRateLimitExceededException(RateLimitExceededException ex) {
        return Result.of(CommonCode.RATE_LIMIT_EXCEEDED, ex.getMessage());
    }

    @ExceptionHandler(DistributedLockAcquisitionException.class)
    public Result<?> handleDistributedLockAcquisitionException(DistributedLockAcquisitionException ex) {
        return Result.of(CommonCode.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return Result.of(CommonCode.MISSING_PARAM);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return Result.of(CommonCode.INVALID_PARAM);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return Result.of(CommonCode.REQUEST_METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleUnknownException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return Result.of(CommonCode.SERVER_ERROR);
    }
}