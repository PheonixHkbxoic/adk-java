package io.github.pheonixhkbxoic.adk.context;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.message.AdkPayload;
import reactor.core.publisher.Flux;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/6 00:44
 * @desc
 */
public class RootContext extends ReadonlyContext {

    public RootContext(AdkPayload payload) {
        this(AdkUtil.uuid4hex(), "root", payload);
    }

    public RootContext(String id, String name, AdkPayload payload) {
        super(null, null);
        this.id = id;
        this.name = name;
        this.payload = payload;
        if (AdkUtil.isEmpty(payload.getTaskId())) {
            payload.setTaskId(AdkUtil.uuid4hex());
        }
        this.response = Flux.empty();
        this.setMetadata(payload.getMetadata());
    }

}
