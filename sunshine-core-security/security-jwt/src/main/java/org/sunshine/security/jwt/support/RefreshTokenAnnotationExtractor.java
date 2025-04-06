package org.sunshine.security.jwt.support;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.sunshine.core.tool.util.ClassUtils;
import org.sunshine.security.core.support.PathPatternRequestMatcher;
import org.sunshine.security.core.support.SecurityAnnotationPathMatcherExtractor;
import org.sunshine.security.jwt.JwtToken;
import org.sunshine.security.jwt.JwtTokenType;
import org.sunshine.security.jwt.annotation.RefreshTokenApi;
import org.sunshine.security.jwt.util.JwtUtils;

/**
 * @author Teamo
 * @since 2025/4/3
 */
public class RefreshTokenAnnotationExtractor extends SecurityAnnotationPathMatcherExtractor {

    /**
     * 统计@RefreshTokenApi注解的数量，保证只能使用一次
     */
    private int refreshTokenApiCount = 0;

    @Override
    protected boolean hasAnnotation(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
        RefreshTokenApi annotation = ClassUtils.getAnnotation(handlerMethod, RefreshTokenApi.class);
        if (annotation == null) {
            return false;
        }
        this.refreshTokenApiCount++;
        Assert.state(this.refreshTokenApiCount < 2,
                String.format("The @RefreshTokenApi annotation can only be used once on method: %s", handlerMethod)
        );
        return true;
    }

    @Override
    public boolean shouldSkipAuthentication(HttpServletRequest request) throws Exception {
        String token = JwtUtils.getToken(request);
        if (token == null) {
            return true;
        }
        JwtToken jwtToken = JwtUtils.getJwtToken(request, token);
        if (jwtToken.getTokenType() == null) {
            return true;
        }
        boolean isRefreshToken = jwtToken.getTokenType().equals(JwtTokenType.REFRESH_TOKEN);
        if (this.pathPatternRequestMatchers.isEmpty()) {
            return isRefreshToken;
        }
        PathPatternRequestMatcher matcher = this.pathPatternRequestMatchers.get(0);
        boolean isMatch = matcher.matches(request);
        return isRefreshToken != isMatch;
    }
}
