package org.sunshine.core.tool.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Teamo
 * @since 2025/4/28
 */
public class WeightedRandomUtils {

    /**
     * 基于权重值随机选择索引
     *
     * @param weights 权重列表（支持任意正数，无需总和为1）
     * @return 选中索引，无匹配时返回-1
     * @throws IllegalArgumentException 输入为空或包含非正权重时抛出
     */
    public static int selectByWeight(List<BigDecimal> weights) {
        if (weights == null || weights.isEmpty()) {
            throw new IllegalArgumentException("The weight list cannot be null or empty");
        }

        BigDecimal totalWeight = weights.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        int scale = weights.stream()
                .map(weight -> Math.max(0, weight.stripTrailingZeros().scale())).max(Integer::compare)
                .orElse(0);
        BigDecimal multiplier = BigDecimal.TEN.pow(scale);
        long randomValue = ThreadLocalRandom.current().nextLong(totalWeight.multiply(multiplier).longValueExact());

        long accumulated = 0;
        for (int i = 0; i < weights.size(); i++) {
            accumulated += weights.get(i).stripTrailingZeros().multiply(multiplier).longValueExact();
            if (randomValue <= accumulated) {
                return i;
            }
        }
        return -1;
    }
}
