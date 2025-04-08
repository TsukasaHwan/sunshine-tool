package org.sunshine.security.jwt.util;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;
import org.sunshine.core.tool.util.WebUtils;
import org.sunshine.security.jwt.core.*;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;
import org.sunshine.security.jwt.support.FastJson2Deserializer;
import org.sunshine.security.jwt.support.FastJson2Serializer;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

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
        AccessToken accessToken = AccessToken.builder()
                .subject(subject)
                .build();
        return accessToken(accessToken);
    }

    /**
     * 访问令牌
     *
     * @param accessToken 访问令牌
     * @return 访问令牌
     */
    public static String accessToken(AccessToken accessToken) {
        return token(accessToken, properties.getExpiresIn());
    }

    /**
     * 刷新令牌
     *
     * @param subject 主题（用户名）
     * @return 刷新令牌
     */
    public static String refreshToken(String subject) {
        Assert.hasText(subject, "'subject' must not be empty");
        RefreshToken refreshToken = RefreshToken.builder()
                .subject(subject)
                .build();
        return refreshToken(refreshToken);
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 刷新令牌
     */
    public static String refreshToken(RefreshToken refreshToken) {
        return token(refreshToken, properties.getRefreshTokenExpiresIn());
    }

    /**
     * 自定义令牌，如果为刷新令牌则使用刷新令牌配置的过期时间，否则使用令牌配置的过期时间
     *
     * @param genericJwtToken 令牌
     * @return 令牌
     */
    public static String token(GenericJwtToken genericJwtToken) {
        Assert.notNull(genericJwtToken, "'token' must not be null");
        Map<String, Object> claims = genericJwtToken.getClaims();
        JwtTokenType tokenType = (JwtTokenType) claims.get(JwtClaimsNames.GRANT_TYPE);
        if (JwtTokenType.REFRESH_TOKEN.equals(tokenType)) {
            return token(genericJwtToken, properties.getRefreshTokenExpiresIn());
        }
        return token(genericJwtToken, properties.getExpiresIn());
    }

    /**
     * 令牌
     *
     * @param token     声明
     * @param expiresIn 过期时间
     * @return 令牌
     */
    private static String token(AbstractToken token, Duration expiresIn) {
        Assert.notNull(token, "'token' must not be null");
        Instant now = Instant.now();
        JwtBuilder jwtBuilder = Jwts.builder()
                .json(new FastJson2Serializer<>())
                .header()
                .add(token.getHeader())
                .and()
                .claims(token.getClaims())
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
        Jws<Claims> jws = Jwts.parser()
                .json(new FastJson2Deserializer<>())
                .verifyWith(properties.getSecret().getPublicKey())
                .clockSkewSeconds(properties.getAllowedClockSkew().getSeconds())
                .build()
                .parseSignedClaims(token);

        return JwtToken.withTokenValue(token)
                .jws(jws)
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
        Claims currentClaims = getCurrentJwtToken().getJws().getPayload();
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
        final String tokenSubject = parseToken(token).getJws().getPayload().getSubject();
        return (tokenSubject != null && tokenSubject.equals(subject));
    }

}
