package org.sunshine.core.tool.datamask.strategy;

import org.sunshine.core.tool.datamask.MaskStrategy;

/**
 * 姓名掩码
 * 规则：
 * 长度小于3，显示最后一位
 * 长度大于3，显示最后两位
 *
 * @author vjtools
 */
public class NameMask extends PartMask implements MaskStrategy {

    @Override
    public String mask(String source, int[] params) {
        if (source == null || source.length() == 0) {
            return source;
        }
        if (source.length() <= 3) {
            return super.mask(source, new int[]{0, 1});
        } else {
            return super.mask(source, new int[]{0, 2});
        }
    }
}
