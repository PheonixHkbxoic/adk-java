package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.Agent;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.node.*;
import io.github.pheonixhkbxoic.adk.event.InMemoryEventService;
import io.github.pheonixhkbxoic.adk.runtime.BranchSelector;
import io.github.pheonixhkbxoic.adk.session.InMemorySessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/7 00:47
 * @desc
 */
public class AgentRouterRunner extends AbstractRunner {
    private final Agent agentRouter;
    private final BranchSelector branchSelector;
    private final Agent agentFallback;
    private final List<Agent> agents;

    public static AgentRouterRunner of(String appName, Agent agentRouter, BranchSelector selector, Agent agentFallback, Agent... agents) {
        return new AgentRouterRunner(appName, agentRouter, selector, agentFallback, Arrays.asList(agents));
    }

    protected AgentRouterRunner(String appName, Agent agentRouter, BranchSelector selector, Agent agentFallback, List<Agent> agents) {
        super(new InMemorySessionService(), new InMemoryEventService(), appName);
        this.agentRouter = agentRouter;
        this.branchSelector = selector;
        this.agentFallback = agentFallback;
        this.agents = agents;
    }


    @Override
    protected Graph buildGraph() {
        // build graph
        End end = End.of();

        // router and edges
        List<Edge> edgeList = agents.stream()
                .map(agent -> {
                    Agentic agentic = Agentic.of(agent.getName(), agent.getAgentInvoker(), end);
                    PlainEdge edge = PlainEdge.of(agent.getName(), agentic);
                    return (Edge) edge;
                })
                .toList();
        Agentic agenticFallback = Agentic.of(agentFallback.getName(), agentFallback.getAgentInvoker(), end);
        ArrayList<Edge> edges = new ArrayList<>(edgeList);
        edges.add(PlainEdge.of(agenticFallback.getName(), agenticFallback, true));
        AgenticRouter agenticRouter = new AgenticRouter(agentRouter.getName(), agentRouter.getAgentInvoker(), edges, branchSelector);

        Start start = Start.of(agenticRouter);
        return new Graph(appName, start);
    }
}
