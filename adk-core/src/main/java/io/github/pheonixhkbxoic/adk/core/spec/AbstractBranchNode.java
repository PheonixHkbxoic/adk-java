package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:40
 * @desc
 */
@Getter
@Setter
public abstract class AbstractBranchNode extends AbstractNode {
    protected List<Edge> edgeList;

    public AbstractBranchNode(String name, String type, NodeInvoker nodeInvoker, List<Edge> edgeList) {
        super(name, type, nodeInvoker);
        this.edgeList = edgeList;
    }


    @Override
    public Mono<ExecuteContext> build(ExecuteContext parent) {


        return null;
    }
}
