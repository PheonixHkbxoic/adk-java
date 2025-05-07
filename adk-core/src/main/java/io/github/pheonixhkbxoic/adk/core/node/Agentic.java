package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractChainNode;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.AgentContext;
import io.github.pheonixhkbxoic.adk.runtime.AgentInvoker;
import lombok.Getter;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:15
 * @desc
 */
@Getter
public class Agentic extends AbstractChainNode {
    protected AgentInvoker agentInvoker;

    public static Agentic of(AgentInvoker invoker, Node next) {
        return of(null, invoker, next);
    }

    public static Agentic of(String name, AgentInvoker agentInvoker, Node next) {
        return new Agentic(name, agentInvoker, PlainEdge.of(next));
    }

    private Agentic(String name, AgentInvoker agentInvoker, Edge edge) {
        super(name, NodeType.AGENT, edge);
        this.agentInvoker = agentInvoker;
    }

    @Override
    public AdkContext buildContextFromParent(AdkContext parent) {
        return new AgentContext(parent, this);
    }
}
