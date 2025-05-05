package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.Payload;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 17:04
 * @desc
 */
public class InvokeContext extends AbstractExecuteContext {

    public InvokeContext(String id, String name, Payload payload) {
        this(id, name, false, payload);
    }

    public InvokeContext(String id, String name, boolean async, Payload payload) {
        super(id, name, async, payload);
    }

}
