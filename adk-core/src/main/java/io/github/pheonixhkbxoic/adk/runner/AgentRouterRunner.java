package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.core.AdkAgentProvider;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.node.*;
import io.github.pheonixhkbxoic.adk.runtime.BranchSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/7 00:47
 * @desc
 */
public class AgentRouterRunner extends AbstractRunner {
    private final AdkAgentProvider adkAgentProviderRouter;
    private final BranchSelector branchSelector;
    private final AdkAgentProvider adkAgentProviderFallback;
    private final List<AdkAgentProvider> adkAgentProviders;

    public static AgentRouterRunner of(String appName, AdkAgentProvider adkAgentProviderRouter, BranchSelector selector, AdkAgentProvider adkAgentProviderFallback, AdkAgentProvider... adkAgentProviders) {
        return new AgentRouterRunner(appName, adkAgentProviderRouter, selector, adkAgentProviderFallback, Arrays.asList(adkAgentProviders));
    }

    protected AgentRouterRunner(String appName, AdkAgentProvider adkAgentProviderRouter, BranchSelector selector, AdkAgentProvider adkAgentProviderFallback, List<AdkAgentProvider> adkAgentProviders) {
        super(appName);
        this.adkAgentProviderRouter = adkAgentProviderRouter;
        this.branchSelector = selector;
        this.adkAgentProviderFallback = adkAgentProviderFallback;
        this.adkAgentProviders = adkAgentProviders;
    }


    @Override
    protected Graph buildGraph() {
        // build graph
        End end = End.of();

        // router and edges
        List<Edge> edgeList = adkAgentProviders.stream()
                .map(adkAgentProvider -> {
                    Agent agent = Agent.of(adkAgentProvider.getName(), adkAgentProvider.getAdkAgentInvoker(), end);
                    PlainEdge edge = PlainEdge.of(adkAgentProvider.getName(), agent);
                    return (Edge) edge;
                })
                .toList();
        Agent agentFallback = Agent.of(adkAgentProviderFallback.getName(), adkAgentProviderFallback.getAdkAgentInvoker(), end);
        ArrayList<Edge> edges = new ArrayList<>(edgeList);
        edges.add(PlainEdge.of(agentFallback.getName(), agentFallback, true));
        AgentRouter agentRouter = new AgentRouter(adkAgentProviderRouter.getName(), adkAgentProviderRouter.getAdkAgentInvoker(), edges, branchSelector);

        Start start = Start.of(agentRouter);
        return new Graph(appName, start);
    }
}
