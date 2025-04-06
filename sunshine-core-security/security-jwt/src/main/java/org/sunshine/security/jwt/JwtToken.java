package org.sunshine.security.jwt;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import org.springframework.util.Assert;

/**
 * @author Teamo
 * @since 2025/4/6
 */
public class JwtToken {

    private final String tokenValue;

    private final Claims claims;

    private final JwtTokenType tokenType;

    public JwtToken(String tokenValue, Claims claims, JwtTokenType tokenType) {
        Assert.hasText(tokenValue, "tokenValue cannot be empty");
        this.tokenValue = tokenValue;
        this.claims = claims;
        this.tokenType = tokenType;
    }

    public static Builder withTokenValue(String tokenValue) {
        return new Builder(tokenValue);
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public Claims getClaims() {
        return claims;
    }

    public JwtTokenType getTokenType() {
        return tokenType;
    }

    public static class Builder {

        private String tokenValue;

        private Claims claims;

        private JwtTokenType tokenType;

        private Builder(String tokenValue) {
            this.tokenValue = tokenValue;
        }

        public Builder tokenValue(String tokenValue) {
            this.tokenValue = tokenValue;
            return this;
        }

        public Builder claims(Claims claims) {
            this.claims = claims;
            return this;
        }

        public Builder tokenType(JwtTokenType tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public JwtToken build() {
            if (claims != null) {
                tokenType = getTokenType(claims.get(JwtClaimsNames.GRANT_TYPE));
            }
            return new JwtToken(tokenValue, claims, tokenType);
        }

        private JwtTokenType getTokenType(Object jwtTokenType) {
            return jwtTokenType == null ? null : JSON.to(JwtTokenType.class, jwtTokenType);
        }
    }
}
