package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.spec.ChainNode;
import io.github.pheonixhkbxoic.adk.core.spec.Node;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 01:02
 * @desc
 */
public class StartNode extends ChainNode {
    public static StartNode of(Node next) {
        PlainEdge edge = PlainEdge.of(next);
        return new StartNode(edge);
    }

    private StartNode(Edge edge) {
        super(null, NodeType.START, null, edge);
    }
}
