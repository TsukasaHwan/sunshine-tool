package org.sunshine.core.tool.enums;

/**
 * Spring Security Filter 默认为 -100
 * OrderedRequestContextFilter 默认为 -105，用于国际化上下文等等
 * <p>
 * 值越小越先执行
 *
 * @author Teamo
 * @since 2023/3/28
 */
public enum WebFilterOrderEnum {

    /**
     * 跨域过滤器
     */
    CORS_FILTER(Integer.MIN_VALUE),

    /**
     * 链路追踪过滤器
     */
    TRACE_FILTER(CORS_FILTER.getOrder() + 1),

    /**
     * xss过滤器
     */
    XSS_FILTER(-102);

    private final int order;

    WebFilterOrderEnum(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
