package io.github.pheonixhkbxoic.adk.context;

import io.github.pheonixhkbxoic.adk.core.spec.Node;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/7 15:06
 * @desc
 */
public class ReadonlyContext extends AbstractAdkContext {
    public ReadonlyContext(AdkContext parent, Node node) {
        super(parent, node);
    }
}
