package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:40
 * @desc
 */
@Getter
@Setter
public abstract class AbstractBranchesNode extends AbstractNode {
    protected List<Edge> edgeList;

    public AbstractBranchesNode(String name, String type, List<Edge> edgeList) {
        super(name, type);
        this.edgeList = edgeList;
    }

}
