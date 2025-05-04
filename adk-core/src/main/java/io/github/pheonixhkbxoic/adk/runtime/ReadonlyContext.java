package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.Payload;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 18:32
 * @desc
 */
public class ReadonlyContext extends AbstractExecuteContext {
    public ReadonlyContext(String name, Payload payload) {
        this(name, false, payload);
    }

    public ReadonlyContext(String name, boolean async, Payload payload) {
        super(name, async, payload);
    }
}
