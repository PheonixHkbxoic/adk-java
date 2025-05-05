package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.Payload;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 18:32
 * @desc
 */
public class ReadonlyContext extends InvokeContext {
    public ReadonlyContext(String id, String name, Payload payload) {
        this(id, name, false, payload);
    }

    public ReadonlyContext(String id, String name, boolean async, Payload payload) {
        super(id, name, async, payload);
    }
}
