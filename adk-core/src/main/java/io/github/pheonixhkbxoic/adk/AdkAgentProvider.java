package io.github.pheonixhkbxoic.adk;

import io.github.pheonixhkbxoic.adk.core.Adk;
import io.github.pheonixhkbxoic.adk.runtime.AdkAgentInvoker;
import lombok.Data;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 16:07
 * @desc
 */
@Data
public class AdkAgentProvider implements Adk {
    private String id;
    private String name;

    private AdkAgentInvoker adkAgentInvoker;

    public static AdkAgentProvider create(String name, AdkAgentInvoker adkAgentInvoker) {
        AdkAgentProvider adkAgentProvider = new AdkAgentProvider();
        adkAgentProvider.setId(AdkUtil.uuid4hex());
        adkAgentProvider.setName(name);
        adkAgentProvider.setAdkAgentInvoker(adkAgentInvoker);
        return adkAgentProvider;
    }
}
