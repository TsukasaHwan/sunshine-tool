package org.sunshine.core.tool.datamask;

/**
 * 脱敏策略接口
 *
 * @author vjtools
 */
public interface MaskStrategy {

    /**
     * 脱敏逻辑
     */
    String mask(String source, int[] params);
}
