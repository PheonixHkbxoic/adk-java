package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractChainNode;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:47
 * @desc
 */
public class Aggregator extends AbstractChainNode {

    public Aggregator(String name, NodeInvoker invoker, Edge edge) {
        super(name, NodeType.AGGREGATOR, invoker, edge);
    }

}
