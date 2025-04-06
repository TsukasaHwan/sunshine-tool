package org.sunshine.security.jwt.util;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;
import org.sunshine.core.tool.util.WebUtils;
import org.sunshine.security.jwt.JwtToken;
import org.sunshine.security.jwt.JwtTokenClaims;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;
import org.sunshine.security.jwt.support.FastJson2Deserializer;
import org.sunshine.security.jwt.support.FastJson2Serializer;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * @author Teamo
 * @since 2023/3/26
 */
public class JwtUtils {

    private static final String JWT_CLAIMS_REQUEST_ATTRIBUTE = "JWT_CLAIMS";

    private static final char TOKEN_CONNECTOR_CHAT = ' ';

    private static JwtSecurityProperties properties;

    public JwtUtils(JwtSecurityProperties properties) {
        JwtUtils.properties = properties;
    }

    /**
     * 访问令牌
     *
     * @param subject 主题（用户名）
     * @return 访问令牌
     */
    public static String accessToken(String subject) {
        Assert.hasText(subject, "'subject' must not be empty");
        JwtTokenClaims.AccessTokenBuilder accessBuilder = JwtTokenClaims.builder()
                .subject(subject)
                .accessToken();
        return accessToken(accessBuilder);
    }

    /**
     * 访问令牌
     *
     * @param accessBuilder 声明
     * @return 访问令牌
     */
    public static String accessToken(JwtTokenClaims.AccessTokenBuilder accessBuilder) {
        Assert.notNull(accessBuilder, "'accessBuilder' must not be null");
        JwtTokenClaims claims = accessBuilder.build();
        return token(claims, properties.getExpiresIn());
    }

    /**
     * 刷新令牌
     *
     * @param subject 主题（用户名）
     * @return 刷新令牌
     */
    public static String refreshToken(String subject) {
        Assert.hasText(subject, "'subject' must not be empty");
        JwtTokenClaims.RefreshTokenBuilder refreshBuilder = JwtTokenClaims.builder()
                .subject(subject)
                .refreshToken();
        return refreshToken(refreshBuilder);
    }

    /**
     * 刷新令牌
     *
     * @param refreshBuilder 声明
     * @return 刷新令牌
     */
    public static String refreshToken(JwtTokenClaims.RefreshTokenBuilder refreshBuilder) {
        Assert.notNull(refreshBuilder, "'refreshBuilder' must not be null");
        JwtTokenClaims claims = refreshBuilder.build();
        return token(claims, properties.getRefreshTokenExpiresIn());
    }

    /**
     * 令牌
     *
     * @param claims    声称要设置为 JWT 主体
     * @param expiresIn 过期时间
     * @return 令牌
     */
    private static String token(JwtTokenClaims claims, Duration expiresIn) {
        Assert.notNull(claims, "'claims' must not be null");
        Instant now = Instant.now();
        JwtBuilder jwtBuilder = Jwts.builder()
                .json(new FastJson2Serializer<>())
                .claims(claims.getClaims())
                .issuedAt(Date.from(now))
                .signWith(properties.getSecret().getPrivateKey());

        if (expiresIn != null) {
            jwtBuilder.expiration(Date.from(now.plus(expiresIn)));
        }

        return jwtBuilder.compact();
    }

    /**
     * 解析令牌
     *
     * @param token 令牌
     * @return JwtToken
     */
    public static JwtToken parseToken(String token) {
        Assert.hasText(token, "'token' must not be empty");
        Claims claims = Jwts.parser()
                .json(new FastJson2Deserializer<>())
                .verifyWith(properties.getSecret().getPublicKey())
                .clockSkewSeconds(properties.getAllowedClockSkew().getSeconds())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return JwtToken.withTokenValue(token)
                .claims(claims)
                .build();
    }

    /**
     * 获取令牌请求标头
     *
     * @return 令牌请求标头
     */
    public static String getTokenHeader() {
        return properties.getHeader();
    }

    /**
     * 获取令牌前缀
     *
     * @return 令牌前缀
     */
    public static String getTokenPrefix() {
        return properties.getTokenPrefix() != null ? properties.getTokenPrefix() + TOKEN_CONNECTOR_CHAT : null;
    }

    /**
     * 获取指定请求的令牌
     *
     * @param request HttpServletRequest
     * @return 令牌
     */
    public static String getToken(HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest must not be null");
        String authToken = request.getHeader(getTokenHeader());
        if (authToken == null || authToken.isBlank()) {
            return null;
        }
        String tokenPrefix = getTokenPrefix();
        return tokenPrefix == null ? authToken : authToken.startsWith(tokenPrefix) ? authToken.substring(tokenPrefix.length()).trim() : null;
    }

    /**
     * 获取当前请求的令牌
     *
     * @return 令牌
     */
    public static String getCurrentToken() {
        return getToken(WebUtils.getRequest());
    }

    /**
     * 获取指定请求的声明
     *
     * @param request HttpServletRequest
     * @param token   令牌
     * @return JwtToken
     */
    public static JwtToken getJwtToken(HttpServletRequest request, String token) {
        Assert.notNull(request, "HttpServletRequest must not be null");
        JwtToken jwtToken = (JwtToken) request.getAttribute(JWT_CLAIMS_REQUEST_ATTRIBUTE);
        if (jwtToken == null) {
            jwtToken = parseToken(token);
            request.setAttribute(JWT_CLAIMS_REQUEST_ATTRIBUTE, jwtToken);
        }
        return jwtToken;
    }

    /**
     * 获取当前请求的凭证
     *
     * @return JwtToken
     */
    public static JwtToken getCurrentJwtToken() {
        return getJwtToken(WebUtils.getRequest(), getCurrentToken());
    }

    /**
     * 获取当前请求的声明值
     *
     * @param name      声明名称
     * @param claimType 声明类型
     * @param <T>       声明类型
     * @return 声明值
     */
    public static <T> T getCurrentClaimValue(String name, Class<T> claimType) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(claimType, "Claim type must not be null");
        Claims currentClaims = getCurrentJwtToken().getClaims();
        return JSON.to(claimType, currentClaims.get(name));
    }

    /**
     * 校验令牌
     *
     * @param token   令牌
     * @param subject 主题（用户名）
     * @return boolean
     */
    public static boolean validateToken(String token, String subject) {
        final String tokenSubject = parseToken(token).getClaims().getSubject();
        return (tokenSubject != null && tokenSubject.equals(subject));
    }

}
