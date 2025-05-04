package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.spec.BranchNode;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:46
 * @desc
 */
public class ScatterNode extends BranchNode {
    public ScatterNode(String name, String type, NodeInvoker nodeInvoker, List<Edge> edgeList) {
        super(name, type, nodeInvoker, edgeList);
    }
}
