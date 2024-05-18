package org.sunshine.core.tool.node;

import java.io.Serial;

/**
 * 森林节点类
 *
 * @author Chill
 */
public class ForestNode extends BaseNode<ForestNode> {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 节点内容
     */
    private Object content;

    public ForestNode(Long id, Long parentId, Object content) {
        this.id = id;
        this.parentId = parentId;
        this.content = content;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
