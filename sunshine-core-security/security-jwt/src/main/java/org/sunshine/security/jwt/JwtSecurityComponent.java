package org.sunshine.security.jwt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.sunshine.security.jwt.properties.JwtSecurityProperties;
import org.sunshine.security.jwt.support.RefreshTokenAnnotationExtractor;

/**
 * @author Teamo
 * @since 2025/4/3
 */
public class JwtSecurityComponent {

    @Bean
    @ConditionalOnProperty(name = "jwt.security.enabled-refresh-token-api-annotation", havingValue = "true")
    public RefreshTokenAnnotationExtractor refreshTokenAnnotationExtractor(JwtSecurityProperties jwtSecurityProperties) {
        return new RefreshTokenAnnotationExtractor(jwtSecurityProperties.getRefreshTokenClaim());
    }

}
