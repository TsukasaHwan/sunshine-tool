package org.sunshine.security.jwt.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.sunshine.core.tool.api.code.CommonCode;
import org.sunshine.core.tool.api.response.Result;
import org.sunshine.core.tool.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 访问被拒绝处理程序
 *
 * @author Teamo
 * @since 2023/3/13
 */
public class JwtTokenAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        WebUtils.renderJson(response, Result.fail(CommonCode.UNAUTHORIZED));
    }
}
