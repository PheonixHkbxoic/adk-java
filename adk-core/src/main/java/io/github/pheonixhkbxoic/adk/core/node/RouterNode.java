package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.spec.BranchNode;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:45
 * @desc
 */
public class RouterNode extends BranchNode {

    public RouterNode(String name, NodeInvoker nodeInvoker, List<Edge> edgeList) {
        super(name, NodeType.ROUTER, nodeInvoker, edgeList);
    }

}
