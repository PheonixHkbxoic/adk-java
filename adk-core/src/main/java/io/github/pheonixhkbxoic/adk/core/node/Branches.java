package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractBranchNode;
import io.github.pheonixhkbxoic.adk.runtime.BranchSelector;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:45
 * @desc
 */
public class Branches extends AbstractBranchNode {
    protected BranchSelector selector;

    public Branches(String name, NodeInvoker nodeInvoker, List<Edge> edgeList, BranchSelector selector) {
        super(name, NodeType.ROUTER, nodeInvoker, edgeList);
        this.selector = selector;
    }

}
