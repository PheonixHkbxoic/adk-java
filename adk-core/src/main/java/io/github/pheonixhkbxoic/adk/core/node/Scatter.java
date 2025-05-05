package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractBranchNode;
import io.github.pheonixhkbxoic.adk.runtime.AbstractBranchesExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:46
 * @desc parallel
 */
public class Scatter extends AbstractBranchNode {
    
    public Scatter(String name, NodeInvoker nodeInvoker, List<Edge> edgeList) {
        super(name, NodeType.SCATTER, nodeInvoker, edgeList);
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
                    return entries.stream().toList().get(0);
                })
                .toList();
    }
}
