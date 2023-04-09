package org.sunshine.core.tool.api.response;

import org.sunshine.core.tool.api.code.CommonCode;
import org.sunshine.core.tool.api.code.ResultCode;
import org.sunshine.core.tool.util.ObjectUtils;

import java.util.Optional;

/**
 * @author Temo
 * @since 2020/10/23
 */
public class Result<T> implements Response {

    private int code;

    private String msg;

    private T data;

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private Result(ResultCode resultCode) {
        this(resultCode, resultCode.msg(), null);
    }

    private Result(ResultCode resultCode, T data) {
        this(resultCode, resultCode.msg(), data);
    }

    private Result(ResultCode resultCode, String msg) {
        this(resultCode, msg, null);
    }

    private Result(ResultCode resultCode, String msg, T data) {
        this(resultCode.code(), msg, data);
    }

    public static <T> Result<T> ok() {
        return new Result<>(CommonCode.SUCCESS);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(CommonCode.SUCCESS, data);
    }

    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(CommonCode.SUCCESS, msg, data);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(CommonCode.FAIL, msg);
    }

    public static <T> Result<T> fail(String msg, T data) {
        return new Result<>(CommonCode.FAIL, msg, data);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode);
    }

    public static <T> Result<T> fail(ResultCode resultCode, String msg) {
        return new Result<>(resultCode, msg);
    }

    public static <T> Result<T> fail() {
        return new Result<>(CommonCode.FAIL);
    }

    public static boolean isSuccess(Result<?> result) {
        return Optional.ofNullable(result)
                .map(x -> ObjectUtils.nullSafeEquals(SUCCESS_CODE, x.code))
                .orElse(Boolean.FALSE);
    }

    public static boolean isNotSuccess(Result<?> result) {
        return !isSuccess(result);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
