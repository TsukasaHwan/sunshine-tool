package org.sunshine.security.jwt.authentication;

import io.github.tsukasahwan.jwt.exception.AccessTokenBlacklistedException;
import io.github.tsukasahwan.jwt.exception.ExpiredJwtException;
import io.github.tsukasahwan.jwt.exception.InvalidTokenException;
import io.github.tsukasahwan.jwt.exception.RefreshTokenRevokedException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.sunshine.core.tool.api.code.CommonCode;
import org.sunshine.core.tool.api.response.Result;
import org.sunshine.core.tool.util.WebUtils;
import org.sunshine.security.core.authentication.CommonAuthenticationEntryPoint;

/**
 * 认证失败处理类
 *
 * @author Teamo
 * @since 2023/3/13
 */
public class JwtAuthenticationEntryPoint extends CommonAuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    protected void handleOtherException(HttpServletResponse response, AuthenticationException authException) {
        if (authException instanceof ExpiredJwtException) {
            WebUtils.renderJson(response, Result.of(CommonCode.TOKEN_EXPIRED, CommonCode.TOKEN_EXPIRED.msg(), CommonCode.TOKEN_EXPIRED.name()));
        } else if (authException instanceof InvalidTokenException
                   || authException instanceof AccessTokenBlacklistedException
                   || authException instanceof RefreshTokenRevokedException) {
            if (authException instanceof InvalidTokenException) {
                log.error("无效令牌", authException);
            }
            WebUtils.renderJson(response, Result.of(CommonCode.INVALID_TOKEN));
        } else {
            log.error("认证失败", authException);
            super.handleOtherException(response, authException);
        }
    }
}
