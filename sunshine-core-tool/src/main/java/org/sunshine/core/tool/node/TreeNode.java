package org.sunshine.core.tool.node;

/**
 * 树型节点类
 *
 * @author Chill
 */
public class TreeNode extends BaseNode {

    private static final long serialVersionUID = 1L;

    private String title;

    private Long key;

    private Long value;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
