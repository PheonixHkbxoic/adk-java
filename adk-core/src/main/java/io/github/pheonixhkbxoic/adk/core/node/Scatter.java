package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractBranchesNode;
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.ExecutableContext;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:46
 * @desc parallel
 */
public class Scatter extends AbstractBranchesNode {

    public Scatter(String name, List<Edge> edgeList) {
        super(name, NodeType.SCATTER, edgeList);
    }

    @Override
    public AdkContext buildContextFromParent(AdkContext parent) {
        return new ExecutableContext(parent, this);
    }
}
