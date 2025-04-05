package org.sunshine.security.jwt.util;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;
import org.sunshine.core.tool.util.WebUtils;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;
import org.sunshine.security.jwt.support.FastJson2Deserializer;
import org.sunshine.security.jwt.support.FastJson2Serializer;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * @author Teamo
 * @since 2023/3/26
 */
public class JwtUtils {

    private static final String JWT_CLAIMS_REQUEST_ATTRIBUTE = "JWT_CLAIMS";

    private static final String REFRESH_TOKEN_CLAIM_NAME = "refresh";

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
        return accessToken(subject, null);
    }

    /**
     * 访问令牌
     *
     * @param subject 主题（用户名）
     * @param claims  声称要设置为 JWT 主体
     * @return 访问令牌
     */
    public static String accessToken(String subject, Map<String, ?> claims) {
        return token(subject, properties.getExpiresIn(), claims);
    }

    /**
     * 刷新令牌
     *
     * @param subject 主题（用户名）
     * @return 刷新令牌
     */
    public static String refreshToken(String subject) {
        Duration refreshTokenExpiresIn = properties.getRefreshTokenExpiresIn();
        Map<String, String> claims = Collections.singletonMap(REFRESH_TOKEN_CLAIM_NAME, properties.getRefreshTokenClaim());
        return token(subject, refreshTokenExpiresIn, claims);
    }

    /**
     * 令牌
     *
     * @param subject   主题（用户名）
     * @param expiresIn 过期时间
     * @param claims    声称要设置为 JWT 主体
     * @return 令牌
     */
    public static String token(String subject, Duration expiresIn, Map<String, ?> claims) {
        Assert.hasText(subject, "'subject' must not be empty");
        Instant now = Instant.now();
        JwtBuilder jwtBuilder = Jwts.builder()
                .json(new FastJson2Serializer<>())
                .issuedAt(Date.from(now))
                .subject(subject)
                .signWith(properties.getSecret().getPrivateKey());

        if (expiresIn != null) {
            jwtBuilder.expiration(Date.from(now.plus(expiresIn)));
        }

        if (claims != null) {
            jwtBuilder.claims(claims);
        }

        return jwtBuilder.compact();
    }

    /**
     * 解析令牌
     *
     * @param token 令牌
     * @return Claims
     */
    public static Claims parseToken(String token) {
        return parseToken(token, null);
    }

    /**
     * 解析令牌
     *
     * @param token        令牌
     * @param claimTypeMap 声明类型
     * @return Claims
     */
    public static Claims parseToken(String token, Map<String, Class<?>> claimTypeMap) {
        Assert.hasText(token, "'token' must not be empty");
        FastJson2Deserializer<Map<String, ?>> deserializer;
        if (claimTypeMap == null) {
            deserializer = new FastJson2Deserializer<>();
        } else {
            deserializer = new FastJson2Deserializer<>(claimTypeMap);
        }
        return Jwts.parser()
                .json(deserializer)
                .verifyWith(properties.getSecret().getPublicKey())
                .clockSkewSeconds(properties.getAllowedClockSkew().getSeconds())
                .build()
                .parseSignedClaims(token)
                .getPayload();
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
     * @return 声明
     */
    public static Claims getClaims(HttpServletRequest request, String token) {
        Assert.notNull(request, "HttpServletRequest must not be null");
        Claims claims = (Claims) request.getAttribute(JWT_CLAIMS_REQUEST_ATTRIBUTE);
        if (claims == null) {
            claims = parseToken(token);
            request.setAttribute(JWT_CLAIMS_REQUEST_ATTRIBUTE, claims);
        }
        return claims;
    }

    /**
     * 获取当前请求的声明
     *
     * @return 声明
     */
    public static Claims getCurrentClaims() {
        return getClaims(WebUtils.getRequest(), getCurrentToken());
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
        Claims currentClaims = getCurrentClaims();
        return JSON.to(claimType, currentClaims.get(name));
    }

    /**
     * 获取刷新令牌声明
     *
     * @param claims 声明
     * @return 刷新令牌声明
     */
    public static String getRefreshTokenClaim(Claims claims) {
        return claims == null ? null : claims.get(REFRESH_TOKEN_CLAIM_NAME, String.class);
    }

    /**
     * 校验令牌
     *
     * @param token   令牌
     * @param subject 主题（用户名）
     * @return boolean
     */
    public static boolean validateToken(String token, String subject) {
        final String tokenSubject = parseToken(token).getSubject();
        return (tokenSubject != null && tokenSubject.equals(subject));
    }

}
