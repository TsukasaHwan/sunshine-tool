package org.sunshine.enums.scanner.context;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * @author sneb
 * @description 码表扫描配置属性类
 * @since 2021-09-09 14:18
 **/
@ConfigurationProperties(prefix = "enum-scan")
public class EnumScanProperties {

    /**
     * 扫描的包路径
     */
    private List<String> scanPackages = Collections.singletonList("com");

    public List<String> getScanPackages() {
        return scanPackages;
    }

    public void setScanPackages(List<String> scanPackages) {
        this.scanPackages = scanPackages;
    }
}
