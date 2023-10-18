package org.sunshine.core.tool.node;

import java.util.ArrayList;
import java.util.List;

/**
 * 节点基类
 *
 * @author Chill
 */
public class BaseNode<T> implements INode<T> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    protected Long id;

    /**
     * 父节点ID
     */
    protected Long parentId;

    /**
     * 子孙节点
     */
    protected List<T> children = new ArrayList<>();

    /**
     * 是否有子孙节点
     */
    private Boolean hasChildren;

    /**
     * 是否有子孙节点
     */
    @Override
    public Boolean getHasChildren() {
        if (children.size() > 0) {
            return true;
        } else {
            return this.hasChildren;
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}
