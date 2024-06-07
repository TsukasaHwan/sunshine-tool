package org.sunshine.core.tool.api.code;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Teamo
 * @since 2019/7/10
 */
public enum CommonCode implements ResultCode {

    /**
     * 操作成功
     */
    SUCCESS(HttpServletResponse.SC_OK, "操作成功"),

    /**
     * 操作失败
     */
    FAIL(HttpServletResponse.SC_BAD_REQUEST, "操作失败"),

    /**
     * 非法参数
     */
    INVALID_PARAM(HttpServletResponse.SC_BAD_REQUEST, "非法参数"),

    /**
     * 请求方法不支持
     */
    REQUEST_METHOD_NOT_SUPPORTED(HttpServletResponse.SC_BAD_REQUEST, "请求方法不支持"),

    /**
     * 系统错误
     */
    SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "系统错误"),

    /**
     * 用户名或密码错误
     */
    INCORRECT_CREDENTIALS(HttpServletResponse.SC_BAD_REQUEST, "用户名或密码错误"),

    /**
     * 权限不足，无权操作
     */
    UNAUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "权限不足，无权操作"),

    /**
     * 认证失败
     */
    AUTHENTICATION_FAILED(HttpServletResponse.SC_UNAUTHORIZED, "认证失败"),

    /**
     * token过期
     */
    TOKEN_EXPIRED(HttpServletResponse.SC_FORBIDDEN, "token过期");

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
