package io.github.pheonixhkbxoic.adk;

import io.github.pheonixhkbxoic.adk.core.Adk;
import io.github.pheonixhkbxoic.adk.runtime.AgentInvoker;
import lombok.Data;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 16:07
 * @desc
 */
@Data
public class Agent implements Adk {
    private String id;
    private String name;

    private AgentInvoker agentInvoker;

    public static Agent create(String name, AgentInvoker agentInvoker) {
        Agent agent = new Agent();
        agent.setId(AdkUtil.uuid4hex());
        agent.setName(name);
        agent.setAgentInvoker(agentInvoker);
        return agent;
    }
}
