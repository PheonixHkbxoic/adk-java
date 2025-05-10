package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.AdkAgentProvider;
import io.github.pheonixhkbxoic.adk.core.node.*;
import io.github.pheonixhkbxoic.adk.core.spec.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/10 17:19
 * @desc
 */
public class AgentLoopRunner extends AbstractRunner {
    private final int maxEpoch;
    private final List<AdkAgentProvider> adkAgentProviderList;

    public static AgentLoopRunner of(String appName, AdkAgentProvider... agentProviders) {
        return of(appName, -1, agentProviders);
    }

    public static AgentLoopRunner of(String appName, int maxEpoch, AdkAgentProvider... agentProviders) {
        return new AgentLoopRunner(appName, maxEpoch, agentProviders);
    }

    protected AgentLoopRunner(String appName, int maxEpoch, AdkAgentProvider... agentProviders) {
        super(appName);
        this.maxEpoch = maxEpoch;
        this.adkAgentProviderList = new ArrayList<>(List.of(agentProviders));
    }

    @Override
    protected Graph buildGraph() {
        End end = End.of();

        Node next = null;
        Collections.reverse(adkAgentProviderList);
        for (AdkAgentProvider adkAgentProvider : adkAgentProviderList) {
            next = Agent.of(adkAgentProvider.getName(), adkAgentProvider.getAdkAgentInvoker(), next);
        }
        Node entry = next;
        Loop loop = new Loop(null, entry, maxEpoch, end);
        Start start = Start.of(loop);

        return new Graph(appName, start);
    }
}
