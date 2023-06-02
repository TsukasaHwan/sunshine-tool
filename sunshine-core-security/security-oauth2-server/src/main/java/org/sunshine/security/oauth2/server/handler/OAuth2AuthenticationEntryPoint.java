package org.sunshine.security.oauth2.server.handler;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.sunshine.core.tool.api.code.AdminCode;
import org.sunshine.core.tool.api.code.CommonCode;
import org.sunshine.core.tool.api.response.Result;
import org.sunshine.core.tool.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Teamo
 * @since 2023/6/2
 */
public class OAuth2AuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        renderExceptionJson(response, authException);
    }

    private void renderExceptionJson(HttpServletResponse response, AuthenticationException authException) {
        if (authException instanceof UsernameNotFoundException) {
            WebUtils.renderJson(response, Result.fail(AdminCode.UNKNOWN_ACCOUNT));
        } else if (authException instanceof BadCredentialsException) {
            WebUtils.renderJson(response, Result.fail(AdminCode.INCORRECT_CREDENTIALS));
        } else {
            WebUtils.renderJson(response, Result.fail(CommonCode.AUTHENTICATION_FAILED));
        }
    }
}
