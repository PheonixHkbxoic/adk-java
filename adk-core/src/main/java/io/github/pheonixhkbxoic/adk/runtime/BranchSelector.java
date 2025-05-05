package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.core.edge.Edge;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/4 22:39
 * @desc
 */
public interface BranchSelector {

    boolean select(Edge edge, int index, int size, AbstractBranchesExecuteContext context);

}
