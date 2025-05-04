package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.Agent;
import io.github.pheonixhkbxoic.adk.session.SessionService;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 15:56
 * @desc
 */
public class AgentRunner extends ChainRunner {
    public static AgentRunner create(SessionService sessionService, String appName, List<Agent> agents) {
        return new AgentRunner(sessionService, appName, agents);
    }

    public static AgentRunner create(SessionService sessionService, String appName, Agent... agents) {
        return new AgentRunner(sessionService, appName, List.of(agents));
    }

    protected AgentRunner(SessionService sessionService, String appName, List<Agent> agents) {
        super(sessionService, appName, agents);
    }

}
