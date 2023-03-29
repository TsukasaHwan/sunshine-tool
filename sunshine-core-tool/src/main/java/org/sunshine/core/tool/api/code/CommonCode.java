package org.sunshine.core.tool.api.code;

import org.sunshine.core.tool.api.response.Response;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Teamo
 * @since 2019/7/10
 */
public enum CommonCode implements ResultCode {

    //操作成功
    SUCCESS(Response.SUCCESS_CODE, Response.SUCCESS),

    //权限不足，无权操作
    UNAUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "权限不足，无权操作"),

    //认证失败
    AUTHENTICATION_FAILED(HttpServletResponse.SC_UNAUTHORIZED, "认证失败"),

    //token过期
    TOKEN_EXPIRED(HttpServletResponse.SC_FORBIDDEN, "token过期"),

    //操作失败
    FAIL(HttpServletResponse.SC_BAD_REQUEST, "操作失败"),

    //请勿频繁操作
    FREQUENT_OPERATION(HttpServletResponse.SC_BAD_REQUEST, "请勿频繁操作"),

    //非法参数
    INVALID_PARAM(HttpServletResponse.SC_BAD_REQUEST, "非法参数"),

    //请求方法不支持
    REQUEST_NOT_SUPPORTED(HttpServletResponse.SC_BAD_REQUEST, "请求方法不支持"),

    //系统错误
    SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "系统错误");

    private final int code;
    private final String msg;

    CommonCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return msg;
    }
}
