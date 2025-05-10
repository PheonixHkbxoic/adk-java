package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.LoopContext;
import lombok.Getter;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/10 16:05
 * @desc
 */
@Getter
public class Loop extends Group {
    protected int maxEpoch;

    public Loop(String name, Node entry, Node next) {
        this(name, entry, -1, next);
    }

    public Loop(String name, Node entry, int maxEpoch, Node next) {
        super(name, entry, next);
        this.maxEpoch = maxEpoch;
    }

    @Override
    public AdkContext buildContextFromParent(AdkContext parent) {
        return new LoopContext(parent, this, this.maxEpoch);
    }
}
