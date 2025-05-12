package io.github.pheonixhkbxoic.adk.context;

import io.github.pheonixhkbxoic.adk.core.spec.Node;
import lombok.Getter;
import lombok.Setter;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/4 23:21
 * @desc
 */
@Setter
@Getter
public class AgentParallelContext extends ScatterContext {

    public AgentParallelContext(AdkContext parent, Node node) {
        super(parent, node);
    }
}
