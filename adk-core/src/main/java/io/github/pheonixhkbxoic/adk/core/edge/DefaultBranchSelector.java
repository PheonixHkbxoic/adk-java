package io.github.pheonixhkbxoic.adk.core.edge;

import io.github.pheonixhkbxoic.adk.runtime.BranchSelector;
import io.github.pheonixhkbxoic.adk.runtime.ExecutableContext;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/5 15:51
 * @desc
 */
public class DefaultBranchSelector implements BranchSelector {
    @Override
    public boolean select(Edge edge, int index, int size, ExecutableContext context) {
        return edge.match(index, size, context);
    }
}
