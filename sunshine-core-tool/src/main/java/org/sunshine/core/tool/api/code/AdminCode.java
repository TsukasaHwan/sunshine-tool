package org.sunshine.core.tool.api.code;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Teamo
 * @since 2020/10/27
 */
public enum AdminCode implements ResultCode {

    /**
     * 未登录
     */
    UN_LOGIN(HttpServletResponse.SC_FORBIDDEN, "未登录"),

    /**
     * 用户不存在
     */
    UNKNOWN_ACCOUNT(HttpServletResponse.SC_BAD_REQUEST, "用户不存在"),

    /**
     * 账号或用户名错误
     */
    INCORRECT_CREDENTIALS(HttpServletResponse.SC_BAD_REQUEST, "账号或用户名错误");

    private final int code;
    private final String msg;

    AdminCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String msg() {
        return this.msg;
    }
}
