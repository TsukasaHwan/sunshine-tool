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
            "/error",
            "/doc.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/*/api-docs/**",
            "/favicon.ico"
    );

    public List<String> getPermitAllPaths() {
        return permitAllPaths;
    }

    public void setPermitAllPaths(List<String> permitAllPaths) {
        this.permitAllPaths = permitAllPaths;
    }
}
