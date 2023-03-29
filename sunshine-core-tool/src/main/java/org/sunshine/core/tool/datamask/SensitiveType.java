package org.sunshine.core.tool.datamask;

import org.sunshine.core.tool.datamask.strategy.EmailMask;
import org.sunshine.core.tool.datamask.strategy.HashMask;
import org.sunshine.core.tool.datamask.strategy.NameMask;
import org.sunshine.core.tool.datamask.strategy.PartMask;

/**
 * 脱敏类型
 *
 * @author vjtools
 */
public enum SensitiveType {
    /**
     * 中文名
     */
    NAME(new NameMask()),

    /**
     * 电话
     */
    PHONE(new PartMask(), 3),

    /**
     * 身份证号
     */
    ID_CARD(new PartMask(), 5, 2),

    /**
     * 银行卡号
     */
    BANKCARD(new PartMask(), 4, 2),

    /**
     * 地址
     */
    ADDRESS(new PartMask(), 9, 0),

    /**
     * 电子邮件
     */
    EMAIL(new EmailMask()),

    /**
     * 验证码
     */
    CAPTCHA(new PartMask(), 1),

    /**
     * 护照/军官证
     */
    PASSPORT(new PartMask(), 2),

    /**
     * 账号
     */
    ACCOUNT(new PartMask(), 1),

    /**
     * 密码
     */
    PASSWORD(new PartMask(), 0),

    /**
     * 散列，这种掩码方式，用户可以手工计算Hash值来精确查询日志。
     */
    HASH(new HashMask()),

    /**
     * 缺省,只显示第一个字符串
     */
    DEFAULT(new PartMask(), 1, 0);

    private final MaskStrategy strategy;
    private final int[] params;

    SensitiveType(MaskStrategy strategy, int... params) {
        this.strategy = strategy;
        this.params = params;
    }

    public MaskStrategy getStrategy() {
        return strategy;
    }

    public int[] getParams() {
        return params;
    }
}
