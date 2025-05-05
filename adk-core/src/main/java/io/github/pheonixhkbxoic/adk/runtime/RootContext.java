package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.Payload;

import java.util.HashMap;
import java.util.Map;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/6 00:44
 * @desc
 */
public class RootContext extends ReadonlyContext {
    private Map<String, ExecuteContext> nonRootExecuteContextMap = new HashMap<>();

    public RootContext(Payload payload) {
        this(false, payload);
    }

    public RootContext(boolean async, Payload payload) {
        this(AdkUtil.uuid4hex(), "root", async, payload);
    }

    public RootContext(String id, String name, boolean async, Payload payload) {
        super(id, name, async, payload);
    }

    public void cache(String contextId, ExecuteContext executeContext) {
        nonRootExecuteContextMap.putIfAbsent(contextId, executeContext);
    }

    public ExecuteContext cache(String contextId) {
        return nonRootExecuteContextMap.get(contextId);
    }
}
