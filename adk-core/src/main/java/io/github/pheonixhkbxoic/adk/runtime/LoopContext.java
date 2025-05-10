package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.core.spec.Node;
import lombok.Getter;
import lombok.Setter;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/10 16:18
 * @desc
 */
@Getter
public class LoopContext extends ReadonlyContext {
    @Setter
    protected int epoch;
    protected int maxEpoch;
    @Getter
    @Setter
    protected boolean breaked;

    public LoopContext(AdkContext parent, Node node, int maxEpoch) {
        super(parent, node);
        this.maxEpoch = maxEpoch;
    }

}
