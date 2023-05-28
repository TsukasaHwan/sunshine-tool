package org.sunshine.core.cache.enums;

/**
 * @author Teamo
 * @since 2023/5/28
 */
public enum LimitType {

    /**
     * 固定窗口
     * 在固定时间段内允许要求的请求数量访问，超过则拒绝
     */
    FIXED_WINDOW,

    /**
     * 滑动窗口
     * 在一定的时间段内允许要求的请求数量访问，超过则拒绝
     */
    SLIDE_WINDOW
}
