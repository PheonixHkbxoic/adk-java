package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractBranchNode;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:46
 * @desc
 */
public class Scatter extends AbstractBranchNode {
    public Scatter(String name, NodeInvoker nodeInvoker, List<Edge> edgeList) {
        super(name, NodeType.SCATTER, nodeInvoker, edgeList);
    }
}
