package io.github.pheonixhkbxoic.adk.core.edge;

import io.github.pheonixhkbxoic.adk.context.AdkContext;
import io.github.pheonixhkbxoic.adk.core.Adk;
import io.github.pheonixhkbxoic.adk.core.spec.Node;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:05
 * @desc
 */
public interface Edge extends Adk {

    boolean match(int index, int size, AdkContext context);

    Node getNode();

}
