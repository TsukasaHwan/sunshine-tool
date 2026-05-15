package org.sunshine.core.enums.conversion;

import org.springframework.boot.persistence.autoconfigure.EntityScan;

/**
 * @author: Teamo
 * @date: 2020/7/13 13:59
 * @description: 用于扫描默认的内置基础转换器, 加载进JPA的转换配置
 */
@EntityScan(basePackages = "org.sunshine.core.enums.conversion.converter")
public class ConverterPackageScan {

}
