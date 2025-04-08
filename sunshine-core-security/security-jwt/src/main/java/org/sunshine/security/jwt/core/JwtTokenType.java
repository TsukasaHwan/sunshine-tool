package org.sunshine.security.jwt.core;

import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * @author Teamo
 * @since 2025/4/6
 */
public class JwtTokenType implements Serializable {

    public static final JwtTokenType ACCESS_TOKEN = new JwtTokenType("access_token");

    public static final JwtTokenType REFRESH_TOKEN = new JwtTokenType("refresh_token");

    private final String value;

    /**
     * Constructs an {@code JwtTokenType} using the provided value.
     *
     * @param value the value of the token type
     */
    public JwtTokenType(String value) {
        Assert.hasText(value, "value cannot be empty");
        this.value = value;
    }

    /**
     * Returns the value of the token type.
     *
     * @return the value of the token type
     */
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        JwtTokenType that = (JwtTokenType) obj;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
}
