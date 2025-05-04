package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.Payload;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 17:04
 * @desc
 */
public class InvokeContext extends AbstractExecuteContext {

    public InvokeContext(String name, Payload payload) {
        this(name, false, payload);
    }

    public InvokeContext(String name, boolean async, Payload payload) {
        super(name, async, payload);
    }

}
