package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.spec.ChainNode;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 01:03
 * @desc
 */
public class EndNode extends ChainNode {
    public static EndNode of() {
        return new EndNode();
    }

    private EndNode() {
        super(null, NodeType.END, null, null);
    }
}
