package org.sunshine.core.tool.api.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Teamo
 * @since 2020/10/27
 */
@Schema(description = "查询条件")
public class Query {

    public static final Integer DEFAULT_CURRENT = 1;

    public static final Integer DEFAULT_SIZE = 10;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer current;

    /**
     * 每页的数量
     */
    @Schema(description = "每页的数量")
    private Integer size;

    /**
     * 正序排序字段名
     */
    @Schema(description = "正序排序字段名(多个以,分割)")
    private String ascs;

    /**
     * 倒序排序字段名
     */
    @Schema(description = "倒序排序字段名(多个以,分割)")
    private String descs;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getAscs() {
        return ascs;
    }

    public void setAscs(String ascs) {
        this.ascs = ascs;
    }

    public String getDescs() {
        return descs;
    }

    public void setDescs(String descs) {
        this.descs = descs;
    }
}
