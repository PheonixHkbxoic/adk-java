package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;
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
public abstract class BranchNode extends AbstractNode {
    protected List<Edge> edgeList;

    public BranchNode(String name, String type, NodeInvoker nodeInvoker, List<Edge> edgeList) {
        super(name, type, nodeInvoker);
        this.edgeList = edgeList;
    }

}
