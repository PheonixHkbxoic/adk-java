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
public class AgentProvider implements Adk {
    private String id;
    private String name;

    private AgentInvoker agentInvoker;

    public static AgentProvider create(String name, AgentInvoker agentInvoker) {
        AgentProvider agentProvider = new AgentProvider();
        agentProvider.setId(AdkUtil.uuid4hex());
        agentProvider.setName(name);
        agentProvider.setAgentInvoker(agentInvoker);
        return agentProvider;
    }
}
