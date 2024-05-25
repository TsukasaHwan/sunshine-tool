package org.sunshine.security.jwt.handler;

import org.springframework.security.core.AuthenticationException;
import org.sunshine.core.tool.api.code.CommonCode;
import org.sunshine.core.tool.api.response.Result;
import org.sunshine.core.tool.util.WebUtils;
import org.sunshine.security.core.handler.CommonAuthenticationEntryPoint;
import org.sunshine.security.jwt.exception.ExpiredJwtAuthenticationException;

import javax.servlet.http.HttpServletResponse;

/**
 * 认证失败处理类
 *
 * @author Teamo
 * @since 2023/3/13
 */
public class JwtAuthenticationEntryPoint extends CommonAuthenticationEntryPoint {

    @Override
    protected void handleOtherException(HttpServletResponse response, AuthenticationException authException) {
        if (authException instanceof ExpiredJwtAuthenticationException) {
            WebUtils.renderJson(response, Result.of(CommonCode.TOKEN_EXPIRED));
        } else {
            super.handleOtherException(response, authException);
        }
    }
}
