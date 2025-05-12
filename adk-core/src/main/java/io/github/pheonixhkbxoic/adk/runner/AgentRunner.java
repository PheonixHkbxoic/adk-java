package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.core.AdkAgentProvider;
import io.github.pheonixhkbxoic.adk.core.node.Agent;
import io.github.pheonixhkbxoic.adk.core.node.End;
import io.github.pheonixhkbxoic.adk.core.node.Graph;
import io.github.pheonixhkbxoic.adk.core.node.Start;
import io.github.pheonixhkbxoic.adk.core.spec.Node;

import java.util.Arrays;
import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/7 00:47
 * @desc
 */
public class AgentRunner extends AbstractRunner {
    private final List<AdkAgentProvider> adkAgentProviders;

    public static AgentRunner of(String appName, AdkAgentProvider... adkAgentProviders) {
        return new AgentRunner(appName, Arrays.asList(adkAgentProviders));
    }

    protected AgentRunner(String appName, List<AdkAgentProvider> adkAgentProviders) {
        super(appName);
        this.adkAgentProviders = adkAgentProviders;
    }

    @Override
    protected Graph buildGraph() {
        // build graph
        Node chain = End.of();
        for (int i = adkAgentProviders.size() - 1; i >= 0; i--) {
            AdkAgentProvider adkAgentProvider = adkAgentProviders.get(i);
            chain = Agent.of(adkAgentProvider.getName(), adkAgentProvider.getAdkAgentInvoker(), chain);
        }
        Start start = Start.of(chain);
        return new Graph(appName, start);
    }


}
