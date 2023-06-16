package org.sunshine.core.tool.api.response;

import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

/**
 * @author Teamo
 * @since 2019/7/10
 */
public interface Response extends Serializable {

    int SUCCESS_CODE = HttpServletResponse.SC_OK;

    String SUCCESS = "操作成功";
}
