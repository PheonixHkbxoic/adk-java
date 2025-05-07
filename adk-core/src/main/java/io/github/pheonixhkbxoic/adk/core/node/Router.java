package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.DefaultBranchSelector;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractBranchesNode;
import io.github.pheonixhkbxoic.adk.exception.PlainEdgeFallbackCountCheckException;
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.BranchSelector;
import io.github.pheonixhkbxoic.adk.runtime.RouterContext;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:45
 * @desc
 */
@Getter
@Setter
public class Router extends AbstractBranchesNode {
    protected BranchSelector selector;

    public Router(List<Edge> edgeList) {
        this(null, edgeList, null);
    }

    public Router(List<Edge> edgeList, BranchSelector selector) {
        this(null, edgeList, selector);
    }

    public Router(String name, List<Edge> edgeList) {
        this(name, edgeList, null);
    }

    public Router(String name, List<Edge> edgeList, BranchSelector selector) {
        this(name, NodeType.ROUTER, edgeList, selector);
    }

    public Router(String name, String nodeType, List<Edge> edgeList, BranchSelector selector) {
        super(name, nodeType, edgeList);
        this.selector = selector;
        if (selector == null) {
            this.selector = new DefaultBranchSelector();
        }

        if (selector instanceof DefaultBranchSelector) {
            long fallbackCount = edgeList.stream()
                    .filter(e -> e instanceof PlainEdge && ((PlainEdge) e).isFallback())
                    .count();
            if (fallbackCount != 1) {
                throw new PlainEdgeFallbackCountCheckException("The count of fallback PlainEdge must be one");
            }
        }
    }

    @Override
    public AdkContext buildContextFromParent(AdkContext parent) {
        return new RouterContext(parent, this);
    }
}
