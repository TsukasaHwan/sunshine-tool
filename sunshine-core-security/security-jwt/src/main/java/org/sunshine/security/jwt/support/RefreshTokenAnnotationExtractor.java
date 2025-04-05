package org.sunshine.security.jwt.support;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.sunshine.core.tool.util.ClassUtils;
import org.sunshine.security.core.support.PathPatternRequestMatcher;
import org.sunshine.security.core.support.SecurityAnnotationPathMatcherExtractor;
import org.sunshine.security.jwt.annotation.RefreshTokenApi;
import org.sunshine.security.jwt.util.JwtUtils;

import java.util.Optional;

/**
 * @author Teamo
 * @since 2025/4/3
 */
public class RefreshTokenAnnotationExtractor extends SecurityAnnotationPathMatcherExtractor {

    /**
     * 统计@RefreshTokenApi注解的数量，保证只能使用一次
     */
    private int refreshTokenApiCount = 0;

    private final String refreshTokenClaim;

    public RefreshTokenAnnotationExtractor(String refreshTokenClaim) {
        this.refreshTokenClaim = refreshTokenClaim;
    }

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
        Optional<PathPatternRequestMatcher> matcherOpt = this.pathPatternRequestMatchers.stream().findFirst();
        return matcherOpt.map(matcher -> {
            String token = JwtUtils.getToken(request);
            if (token == null) {
                return false;
            }
            Claims claims = JwtUtils.getClaims(request, token);
            String refreshTokenClaim = JwtUtils.getRefreshTokenClaim(claims);
            boolean isMatched = matcher.matches(request);
            return refreshTokenClaim == null ? isMatched : !refreshTokenClaim.equals(this.refreshTokenClaim) || !isMatched;
        }).orElse(Boolean.FALSE);
    }
}
