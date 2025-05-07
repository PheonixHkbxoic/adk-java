package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractChainNode;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:47
 * @desc
 */
public class Aggregator extends AbstractChainNode {
    public Aggregator(String name, Edge edge) {
        super(name, NodeType.AGGREGATOR, edge);
    }

}
