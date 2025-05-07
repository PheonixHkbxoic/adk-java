package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 17:24
 * @desc
 */
@Getter
@Setter
public class AbstractAdkContext implements AdkContext {
    protected Node node;
    protected String id;
    protected String name;
    protected AdkContext activeParent;
    protected AdkContext activeChild;
    protected Payload payload;
    protected Flux<ResponseFrame> response;
    private Map<String, Object> metadata;

    public AbstractAdkContext(AdkContext parent, Node node) {
        if (node != null) {
            this.node = node;
            this.id = node.getId();
            this.name = node.getName();
        }
        if (parent != null) {
            this.activeParent = parent;
            parent.setActiveChild(this);
            this.payload = parent.getPayload();
            this.response = parent.getResponse();
            this.metadata = parent.getMetadata();
        }
    }


    @Override
    public String toString() {
        return this.name + "@" + this.getClass().getSimpleName();
    }

}
