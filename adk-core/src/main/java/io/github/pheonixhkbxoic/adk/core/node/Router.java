package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.DefaultBranchSelector;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractBranchNode;
import io.github.pheonixhkbxoic.adk.exception.PlainEdgeFallbackCountCheckException;
import io.github.pheonixhkbxoic.adk.runtime.AbstractBranchesExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.BranchSelector;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:45
 * @desc
 */
public class Router extends AbstractBranchNode {
    protected BranchSelector selector;

    public Router(List<Edge> edgeList) {
        this(null, null, edgeList, null);
    }

    public Router(List<Edge> edgeList, BranchSelector selector) {
        this(null, null, edgeList, selector);
    }

    public Router(String name, List<Edge> edgeList) {
        this(name, null, edgeList, null);
    }

    public Router(String name, List<Edge> edgeList, BranchSelector selector) {
        this(name, null, edgeList, selector);
    }

    public Router(String name, NodeInvoker nodeInvoker, List<Edge> edgeList, BranchSelector selector) {
        super(name, NodeType.ROUTER, nodeInvoker, edgeList);
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
    protected List<Map.Entry<Edge, ExecuteContext>> select(AbstractBranchesExecuteContext currContext) {
        if (edgeList.size() != currContext.getChildList().size()) {
            String error = String.format("edgeList size %d != %d of childList size of %s",
                    edgeList.size(), currContext.getChildList().size(), currContext.getClass().getSimpleName());
            throw new RuntimeException(error);
        }

        return IntStream.range(0, edgeList.size())
                .mapToObj(i -> {
                    Set<Map.Entry<Edge, ExecuteContext>> entries = Map.of(edgeList.get(i), currContext.getChildList().get(i)).entrySet();
                    Map.Entry<Edge, ExecuteContext> pair = entries.stream().toList().get(0);
                    boolean select = selector.select(pair.getKey(), i, edgeList.size(), currContext);
                    if (select) {
                        return pair;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
