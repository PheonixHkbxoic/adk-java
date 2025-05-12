package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.context.AdkContext;
import io.github.pheonixhkbxoic.adk.context.AgentParallelContext;
import io.github.pheonixhkbxoic.adk.message.ResponseFrame;
import io.github.pheonixhkbxoic.adk.runtime.AdkAgentInvoker;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 01:30
 * @desc
 */
@Getter
public class AgentParallel extends Scatter {
    protected AdkAgentInvoker adkAgentInvoker;
    @Setter
    protected Consumer<Flux<ResponseFrame>> scatterBranchesGenerator;

    public AgentParallel(String name, AdkAgentInvoker adkAgentInvoker) {
        super(name, new ArrayList<>());
        this.adkAgentInvoker = adkAgentInvoker;
    }

    @Override
    public AdkContext buildContextFromParent(AdkContext parent) {
        return new AgentParallelContext(parent, this);
    }
}
