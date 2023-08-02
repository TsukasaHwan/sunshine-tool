package org.sunshine.core.oss.enums;

/**
 * minio策略配置
 *
 * @author SCMOX
 */
public enum PolicyType {

    /**
     * 只读
     */
    READ("read", "只读"),

    /**
     * 只写
     */
    WRITE("write", "只写"),

    /**
     * 读写
     */
    READ_WRITE("read_write", "读写");

    /**
     * 类型
     */
    private final String type;

    /**
     * 描述
     */
    private final String policy;

    PolicyType(String type, String policy) {
        this.type = type;
        this.policy = policy;
    }

    public String getType() {
        return type;
    }

    public String getPolicy() {
        return policy;
    }
}
