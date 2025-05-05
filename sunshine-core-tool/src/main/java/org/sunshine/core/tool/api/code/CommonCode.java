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
     * 无效参数
     */
    INVALID_PARAM(HttpServletResponse.SC_BAD_REQUEST, "无效参数"),

    /**
     * 缺少参数
     */
    MISSING_PARAM(HttpServletResponse.SC_BAD_REQUEST, "缺少参数"),

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
    USERNAME_OR_PASSWORD_ERROR(HttpServletResponse.SC_UNAUTHORIZED, "用户名或密码错误"),

    /**
     * 权限不足，无权操作
     */
    UNAUTHORIZED(HttpServletResponse.SC_FORBIDDEN, "权限不足，无权操作"),

    /**
     * 认证失败
     */
    AUTHENTICATION_FAILED(HttpServletResponse.SC_UNAUTHORIZED, "认证失败"),

    /**
     * 无效的令牌
     */
    INVALID_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "无效的令牌"),

    /**
     * 令牌过期
     */
    TOKEN_EXPIRED(HttpServletResponse.SC_UNAUTHORIZED, "令牌已过期，请重新登录"),

    /**
     * 注销成功
     */
    LOGOUT_SUCCESS(HttpServletResponse.SC_OK, "注销成功"),

    /**
     * 请求频率超过限制，请稍后重试
     */
    RATE_LIMIT_EXCEEDED(429, "请求频率超过限制，请稍后重试"),

    /**
     * 系统繁忙，请稍后再试
     */
    SERVICE_UNAVAILABLE(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "系统繁忙，请稍后再试");

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
