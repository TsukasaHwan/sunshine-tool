package org.sunshine.core.security.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.sunshine.core.security.exception.JwtExpiredException;
import org.sunshine.core.tool.api.code.AdminCode;
import org.sunshine.core.tool.api.code.CommonCode;
import org.sunshine.core.tool.api.response.Result;
import org.sunshine.core.tool.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败处理类
 *
 * @author Teamo
 * @since 2023/3/13
 */
public class JwtTokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        renderExceptionJson(response, authException);
    }

    public void renderExceptionJson(HttpServletResponse response, AuthenticationException authException) {
        if (authException instanceof JwtExpiredException) {
            WebUtils.renderJson(response, Result.fail(CommonCode.TOKEN_EXPIRED));
        } else if (authException instanceof UsernameNotFoundException) {
            WebUtils.renderJson(response, Result.fail(AdminCode.UNKNOWN_ACCOUNT));
        } else {
            WebUtils.renderJson(response, Result.fail(CommonCode.AUTHENTICATION_FAILED));
        }
    }
}
