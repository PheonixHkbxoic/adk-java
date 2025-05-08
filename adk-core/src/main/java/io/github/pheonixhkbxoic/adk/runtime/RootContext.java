package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.Payload;
import reactor.core.publisher.Flux;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/6 00:44
 * @desc
 */
public class RootContext extends ReadonlyContext {

    public RootContext(Payload payload) {
        this(AdkUtil.uuid4hex(), "root", payload);
    }

    public RootContext(String id, String name, Payload payload) {
        super(null, null);
        this.id = id;
        this.name = name;
        this.payload = payload;
        if (AdkUtil.isEmpty(payload.getTaskId())) {
            payload.setTaskId(AdkUtil.uuid4hex());
        }
        this.response = Flux.empty();
    }

}
