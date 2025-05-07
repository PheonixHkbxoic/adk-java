package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractNode;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import lombok.Getter;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/7 00:25
 * @desc
 */
@Getter
public class Graph extends AbstractNode {
    protected Node start;

    public Graph(String name, Node start) {
        super(name, NodeType.GRAPH);
        this.start = start;
    }
}
