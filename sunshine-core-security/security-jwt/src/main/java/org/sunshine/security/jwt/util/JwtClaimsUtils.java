package org.sunshine.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Teamo
 * @since 2023/3/26
 */
public class JwtClaimsUtils {

    public static final String ACCESS_TOKEN_PREFIX = "Bearer ";

    public static final String REFRESH_TOKEN_CLAIM_KEY = "refresh";

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
        Duration expiresIn = properties.getExpiresIn();
        return sign(subject, expiresIn, null);
    }

    /**
     * 刷新令牌
     *
     * @param subject 主题(用户名)
     * @return JWT
     */
    public static String refreshToken(String subject) {
        Duration refreshTokenExpiresIn = properties.getRefreshTokenExpiresIn();
        Map<String, Object> claims = new HashMap<>(1);
        claims.put(REFRESH_TOKEN_CLAIM_KEY, properties.getRefreshTokenClaim());
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
    public static String sign(String subject, Duration expiresIn, Map<String, Object> claims) {
        Instant now = Instant.now();
        JwtBuilder jwtBuilder = Jwts.builder()
                .setIssuedAt(Date.from(now))
                // 主题信息，可存储用户json
                .setSubject(subject)
                .signWith(properties.getSecret().getPrivateKey());

        if (expiresIn != null) {
            jwtBuilder.setExpiration(Date.from(now.plus(expiresIn)));
        }

        if (claims != null) {
            jwtBuilder.addClaims(claims);
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
        // 默认情况下 JJWT 只能解析 String, Date, Long, Integer, Short and Byte 类型，如果需要解析其他类型则需要配置 JacksonDeserializer
        // .deserializeJsonWith(new JacksonDeserializer(Maps.of(USER_INFO_KEY, UserInfo.class).build()))
        return Jwts.parserBuilder()
                .setSigningKey(properties.getSecret().getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
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
        String authHeader = request.getHeader(properties.getHeader());
        if (authHeader == null) {
            return null;
        }
        int indexOf = authHeader.indexOf(ACCESS_TOKEN_PREFIX);
        if (indexOf == -1) {
            return null;
        }
        return authHeader.substring(indexOf + 7).trim();
    }

    /**
     * 获取token请求标头
     *
     * @return token请求标头
     */
    public static String getTokenRequestHeader() {
        return properties.getHeader();
    }
}
