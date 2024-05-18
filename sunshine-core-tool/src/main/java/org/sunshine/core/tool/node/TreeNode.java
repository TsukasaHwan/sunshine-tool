package org.sunshine.core.tool.node;

import java.io.Serial;
import java.util.Objects;

/**
 * 树型节点类
 *
 * @author Chill
 */
public class TreeNode extends BaseNode<TreeNode> {

    @Serial
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TreeNode other = (TreeNode) obj;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentId);
    }
}
