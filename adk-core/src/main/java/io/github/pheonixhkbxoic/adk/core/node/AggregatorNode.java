package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.spec.ChainNode;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:47
 * @desc
 */
public class AggregatorNode extends ChainNode {

    public AggregatorNode(String name, String type, NodeInvoker invoker, Edge edge) {
        super(name, type, invoker, edge);
    }

}
