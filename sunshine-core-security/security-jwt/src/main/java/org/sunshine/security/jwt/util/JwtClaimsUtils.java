package org.sunshine.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.sunshine.security.jwt.Jwt;
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
public class JwtClaimsUtils {

    public static final String REFRESH_TOKEN_CLAIM_KEY = "refresh";

    private static final char TOKEN_CONNECTOR_CHAT = ' ';

    private static JwtSecurityProperties properties;

    public JwtClaimsUtils(JwtSecurityProperties properties) {
        JwtClaimsUtils.properties = properties;
    }

    /**
     * 访问令牌
     *
     * @param subject 主题(用户名)
     * @return JWT
     */
    public static String accessToken(String subject) {
        return accessToken(subject, null);
    }

    /**
     * 访问令牌
     *
     * @param subject 主题(用户名)
     * @param claims  声称要设置为 JWT 主体
     * @return JWT
     */
    public static String accessToken(String subject, Map<String, ?> claims) {
        return sign(subject, properties.getExpiresIn(), claims);
    }

    /**
     * 刷新令牌
     *
     * @param subject 主题(用户名)
     * @return JWT
     */
    public static String refreshToken(String subject) {
        Duration refreshTokenExpiresIn = properties.getRefreshTokenExpiresIn();
        Map<String, String> claims = Collections.singletonMap(REFRESH_TOKEN_CLAIM_KEY, properties.getRefreshTokenClaim());
        return sign(subject, refreshTokenExpiresIn, claims);
    }

    /**
     * 签名
     *
     * @param subject   主题(用户名)
     * @param expiresIn 过期时间
     * @param claims    声称要设置为 JWT 主体
     * @return JWT
     */
    public static String sign(String subject, Duration expiresIn, Map<String, ?> claims) {
        Instant now = Instant.now();
        JwtBuilder jwtBuilder = Jwts.builder()
                .json(new FastJson2Serializer<>())
                .issuedAt(Date.from(now))
                // 主题信息，可存储用户json
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
     * 解析JWT
     *
     * @param token token
     * @return Claims
     */
    public static Claims parseToken(String token) {
        return parseToken(token, null);
    }

    /**
     * 解析JWT
     *
     * @param token        token
     * @param claimTypeMap 声明类型
     * @return Claims
     */
    public static Claims parseToken(String token, Map<String, Class<?>> claimTypeMap) {
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
     * 获取JWT对象
     *
     * @param subject subject
     * @return {@link Jwt}
     */
    public static Jwt getJwt(String subject) {
        return getJwt(subject, null);
    }

    /**
     * 获取JWT对象
     *
     * @param subject subject
     * @param claims  声明要设置为 JWT 主体
     * @return {@link Jwt}
     */
    public static Jwt getJwt(String subject, Map<String, ?> claims) {
        String accessToken = accessToken(subject, claims);
        String refreshToken = refreshToken(subject);
        long time = parseToken(accessToken).getExpiration().getTime();
        return Jwt.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(time)
                .build();
    }

    /**
     * 从JWT中获取用户名
     *
     * @param token token
     * @return username
     */
    public static String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 校验token是否与用户名匹配
     *
     * @param token    token
     * @param username username
     * @return boolean
     */
    public static boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername != null && tokenUsername.equals(username));
    }

    /**
     * 从header中获取JWT
     *
     * @param request HttpServletRequest
     * @return JWT
     */
    public static String getToken(HttpServletRequest request) {
        String authToken = request.getHeader(getTokenRequestHeader());
        if (authToken == null || authToken.isBlank()) {
            return null;
        }
        String tokenPrefix = getTokenPrefix();
        return tokenPrefix == null ? authToken : authToken.startsWith(tokenPrefix) ? authToken.substring(tokenPrefix.length()).trim() : null;
    }

    /**
     * 获取token请求标头
     *
     * @return token请求标头
     */
    public static String getTokenRequestHeader() {
        return properties.getHeader();
    }

    /**
     * 获取token前缀
     *
     * @return token前缀
     */
    public static String getTokenPrefix() {
        return properties.getTokenPrefix() != null ? properties.getTokenPrefix() + TOKEN_CONNECTOR_CHAT : null;
    }
}
