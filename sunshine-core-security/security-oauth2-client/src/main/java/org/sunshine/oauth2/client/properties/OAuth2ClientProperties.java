package org.sunshine.oauth2.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Teamo
 * @since 2023/6/9
 */
@ConfigurationProperties("oauth2.client")
public class OAuth2ClientProperties {

    private List<String> forbiddenPaths;

    public List<String> getForbiddenPaths() {
        return forbiddenPaths;
    }

    public void setForbiddenPaths(List<String> forbiddenPaths) {
        this.forbiddenPaths = forbiddenPaths;
    }
}
