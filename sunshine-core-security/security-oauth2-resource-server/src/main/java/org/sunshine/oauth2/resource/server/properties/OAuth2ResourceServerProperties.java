package org.sunshine.oauth2.resource.server.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * @author Teamo
 * @since 2023/6/5
 */
@ConfigurationProperties("oauth2.resource.server")
public class OAuth2ResourceServerProperties {

    /**
     * Accessible resource path.
     */
    private List<String> permitAllPaths = Arrays.asList(
            "/",
            "/error",
            "/*.html",
            "/*/*.html",
            "/*/*.css",
            "/*/*.js",
            "/profile/**",
            "/favicon.ico",
            "/swagger-resources/**",
            "/webjars/**",
            "/*/api-docs/**"
    );

    public List<String> getPermitAllPaths() {
        return permitAllPaths;
    }

    public void setPermitAllPaths(List<String> permitAllPaths) {
        this.permitAllPaths = permitAllPaths;
    }
}
