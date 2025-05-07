package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.Agent;
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
    private final List<Agent> agents;

    public static AgentRunner of(String appName, Agent... agents) {
        return new AgentRunner(appName, Arrays.asList(agents));
    }

    protected AgentRunner(String appName, List<Agent> agents) {
        super(new InMemorySessionService(), new InMemoryEventService(), appName);
        this.agents = agents;
    }

    @Override
    protected Graph buildGraph() {
        // build graph
        Node chain = End.of();
        for (int i = agents.size() - 1; i >= 0; i--) {
            Agent agent = agents.get(i);
            chain = Agentic.of(agent.getName(), agent.getAgentInvoker(), chain);
        }
        Start start = Start.of(chain);
        return new Graph(appName, start);
    }


}
