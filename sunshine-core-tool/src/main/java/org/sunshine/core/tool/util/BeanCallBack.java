package org.sunshine.core.tool.util;

/**
 * @author Teamo
 * @date 2020/7/3
 */
@FunctionalInterface
public interface BeanCallBack<S, T> {
    /**
     * 定义默认回调方法
     *
     * @param source
     * @param target
     */
    void callBack(S source, T target);
}
