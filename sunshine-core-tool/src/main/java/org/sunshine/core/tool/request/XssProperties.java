package org.sunshine.core.tool.request;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teamo
 * @since 2020/9/17
 */
@ConfigurationProperties("xss")
public class XssProperties {
    /**
     * turn on xss
     */
    private Boolean enabled = true;

    /**
     * ignore url
     */
    private List<String> skipUrl = new ArrayList<>();

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getSkipUrl() {
        return skipUrl;
    }

    public void setSkipUrl(List<String> skipUrl) {
        this.skipUrl = skipUrl;
    }
}
