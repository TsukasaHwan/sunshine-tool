package org.sunshine.core.mp.extension.plugin;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author Teamo
 * @since 2023/5/5
 */
public class DynamicTableSuffixContextHolder {

    private static final TransmittableThreadLocal<String> CONTEXT_HOLDER = new TransmittableThreadLocal<>();

    /**
     * 设置分表后缀
     *
     * @param suffix 后缀
     */
    public static void setTableNameSuffix(String suffix) {
        CONTEXT_HOLDER.set(suffix);
    }

    /**
     * 获取分表后缀
     *
     * @return String
     */
    public static String getTableNameSuffix() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除上下文数据
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
