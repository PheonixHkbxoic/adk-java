package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractChainNode;
import io.github.pheonixhkbxoic.adk.core.spec.Node;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 01:02
 * @desc
 */
public class Start extends AbstractChainNode {
    public static Start of(Node next) {
        PlainEdge edge = PlainEdge.of(next);
        return new Start(edge);
    }

    private Start(Edge edge) {
        super(null, NodeType.START, edge);
    }
}
