package org.sunshine.core.cloud.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.List;

/**
 * @author Teamo
 * @since 2023/6/14
 */
@RefreshScope
@ConfigurationProperties("feign.headers")
public class FeignHeadersProperties {

    private List<String> allowed = Arrays.asList(
            "X-Real-IP",
            "X-Forwarded-For",
            HttpHeaders.AUTHORIZATION
    );

    public List<String> getAllowed() {
        return allowed;
    }

    public void setAllowed(List<String> allowed) {
        this.allowed = allowed;
    }
}
