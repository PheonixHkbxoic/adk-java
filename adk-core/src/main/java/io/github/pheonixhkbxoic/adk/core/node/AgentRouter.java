package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.runtime.AdkAgentInvoker;
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.AgentRouterContext;
import io.github.pheonixhkbxoic.adk.runtime.BranchSelector;
import lombok.Getter;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/6 18:53
 * @desc
 */
@Getter
public class AgentRouter extends Router {
    protected AdkAgentInvoker adkAgentInvoker;


    public AgentRouter(String name, AdkAgentInvoker adkAgentInvoker, List<Edge> edgeList, BranchSelector selector) {
        super(name, NodeType.AGENT_ROUTER, edgeList, selector);
        this.adkAgentInvoker = adkAgentInvoker;
    }

    @Override
    public AdkContext buildContextFromParent(AdkContext parent) {
        return new AgentRouterContext(parent, this);
    }

}
