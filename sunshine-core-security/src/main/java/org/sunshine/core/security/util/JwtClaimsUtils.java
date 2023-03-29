package org.sunshine.core.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.sunshine.core.security.properties.JwtSecurityProperties;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * @author Teamo
 * @since 2023/3/26
 */
public class JwtClaimsUtils {
    public final static String TOKEN_PREFIX = "Bearer ";

    private static JwtSecurityProperties properties;

    public JwtClaimsUtils(JwtSecurityProperties properties) {
        JwtClaimsUtils.properties = properties;
    }

    /**
     * 签名
     *
     * @param subject 主题(用户名)
     * @return JWT
     */
    public static String sign(String subject) {
        Instant now = Instant.now();
        JwtBuilder jwtBuilder = Jwts.builder()
                .setIssuedAt(Date.from(now))
                // 主题信息，可存储用户json
                .setSubject(subject)
                .signWith(properties.getSecret().getPrivateKey());

        Duration expiresIn = properties.getExpiresIn();
        if (expiresIn != null) {
            jwtBuilder.setExpiration(Date.from(now.plus(expiresIn)));
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
                .setAllowedClockSkewSeconds(180L)
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
        int indexOf = authHeader.indexOf(TOKEN_PREFIX);
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
