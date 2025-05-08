package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.AgentProvider;
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
    private final AgentProvider agentProviderRouter;
    private final BranchSelector branchSelector;
    private final AgentProvider agentProviderFallback;
    private final List<AgentProvider> agentProviders;

    public static AgentRouterRunner of(String appName, AgentProvider agentProviderRouter, BranchSelector selector, AgentProvider agentProviderFallback, AgentProvider... agentProviders) {
        return new AgentRouterRunner(appName, agentProviderRouter, selector, agentProviderFallback, Arrays.asList(agentProviders));
    }

    protected AgentRouterRunner(String appName, AgentProvider agentProviderRouter, BranchSelector selector, AgentProvider agentProviderFallback, List<AgentProvider> agentProviders) {
        super(new InMemorySessionService(), new InMemoryEventService(), appName);
        this.agentProviderRouter = agentProviderRouter;
        this.branchSelector = selector;
        this.agentProviderFallback = agentProviderFallback;
        this.agentProviders = agentProviders;
    }


    @Override
    protected Graph buildGraph() {
        // build graph
        End end = End.of();

        // router and edges
        List<Edge> edgeList = agentProviders.stream()
                .map(agentProvider -> {
                    Agentic agentic = Agentic.of(agentProvider.getName(), agentProvider.getAgentInvoker(), end);
                    PlainEdge edge = PlainEdge.of(agentProvider.getName(), agentic);
                    return (Edge) edge;
                })
                .toList();
        Agentic agenticFallback = Agentic.of(agentProviderFallback.getName(), agentProviderFallback.getAgentInvoker(), end);
        ArrayList<Edge> edges = new ArrayList<>(edgeList);
        edges.add(PlainEdge.of(agenticFallback.getName(), agenticFallback, true));
        AgenticRouter agenticRouter = new AgenticRouter(agentProviderRouter.getName(), agentProviderRouter.getAgentInvoker(), edges, branchSelector);

        Start start = Start.of(agenticRouter);
        return new Graph(appName, start);
    }
}
