package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.spec.ChainNode;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.runtime.AgentInvoker;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:15
 * @desc
 */
public class AgentNode extends ChainNode {

    public static AgentNode of(AgentInvoker invoker, Node next) {
        return of(null, invoker, next);
    }

    public static AgentNode of(String name, Node next) {
        return of(name, null, next);
    }

    public static AgentNode of(String name, AgentInvoker invoker, Node next) {
        PlainEdge plainEdge = PlainEdge.of(next);
        return new AgentNode(name, invoker, plainEdge);
    }

    private AgentNode(String name, AgentInvoker invoker, Edge edge) {
        super(name, NodeType.AGENT, invoker, edge);
    }

}
