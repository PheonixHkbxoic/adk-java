package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.AgentProvider;
import io.github.pheonixhkbxoic.adk.core.node.Agentic;
import io.github.pheonixhkbxoic.adk.core.node.End;
import io.github.pheonixhkbxoic.adk.core.node.Graph;
import io.github.pheonixhkbxoic.adk.core.node.Start;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.event.InMemoryEventService;
import io.github.pheonixhkbxoic.adk.session.InMemorySessionService;

import java.util.Arrays;
import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/7 00:47
 * @desc
 */
public class AgentRunner extends AbstractRunner {
    private final List<AgentProvider> agentProviders;

    public static AgentRunner of(String appName, AgentProvider... agentProviders) {
        return new AgentRunner(appName, Arrays.asList(agentProviders));
    }

    protected AgentRunner(String appName, List<AgentProvider> agentProviders) {
        super(new InMemorySessionService(), new InMemoryEventService(), appName);
        this.agentProviders = agentProviders;
    }

    @Override
    protected Graph buildGraph() {
        // build graph
        Node chain = End.of();
        for (int i = agentProviders.size() - 1; i >= 0; i--) {
            AgentProvider agentProvider = agentProviders.get(i);
            chain = Agentic.of(agentProvider.getName(), agentProvider.getAgentInvoker(), chain);
        }
        Start start = Start.of(chain);
        return new Graph(appName, start);
    }


}
