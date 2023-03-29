package org.sunshine.core.tool.api.code;

/**
 * @author Teamo
 * @since 2019/7/10
 */
public interface ResultCode {
    /**
     * 操作码
     *
     * @return 操作码
     */
    int code();

    /**
     * 提示信息
     *
     * @return 提示信息
     */
    String msg();
}
