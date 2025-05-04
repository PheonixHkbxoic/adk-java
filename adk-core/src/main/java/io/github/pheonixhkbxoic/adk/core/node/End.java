package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractChainNode;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 01:03
 * @desc
 */
public class End extends AbstractChainNode {
    public static End of() {
        return new End();
    }

    private End() {
        super(null, NodeType.END, null, null);
    }
}
