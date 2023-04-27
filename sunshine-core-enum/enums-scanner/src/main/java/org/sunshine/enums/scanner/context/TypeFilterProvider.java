package org.sunshine.enums.scanner.context;

import org.springframework.core.type.filter.TypeFilter;

/**
 * @author sneb
 * @description 文件扫描过滤器
 * @since 2021-09-09 14:26
 **/
public interface TypeFilterProvider {

    /**
     * 提供过滤器
     *
     * @return filter
     */
    TypeFilter filter();
}
