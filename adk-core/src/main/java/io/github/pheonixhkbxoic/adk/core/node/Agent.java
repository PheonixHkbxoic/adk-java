package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractChainNode;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.runtime.AdkAgentInvoker;
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.AgentContext;
import lombok.Getter;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:15
 * @desc
 */
@Getter
public class Agent extends AbstractChainNode {
    protected AdkAgentInvoker adkAgentInvoker;

    public static Agent of(AdkAgentInvoker invoker, Node next) {
        return of(null, invoker, next);
    }

    public static Agent of(String name, AdkAgentInvoker adkAgentInvoker, Node next) {
        return new Agent(name, adkAgentInvoker, next == null ? null : PlainEdge.of(next));
    }

    private Agent(String name, AdkAgentInvoker adkAgentInvoker, Edge edge) {
        super(name, NodeType.AGENT, edge);
        this.adkAgentInvoker = adkAgentInvoker;
    }

    @Override
    public AdkContext buildContextFromParent(AdkContext parent) {
        return new AgentContext(parent, this);
    }
}
