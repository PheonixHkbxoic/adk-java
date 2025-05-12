package io.github.pheonixhkbxoic.adk.context;

import io.github.pheonixhkbxoic.adk.core.State;
import io.github.pheonixhkbxoic.adk.core.Status;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.message.AdkPayload;
import io.github.pheonixhkbxoic.adk.message.ResponseFrame;
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
    protected LoopContext loopContext;
    protected AdkPayload payload;
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

            if (parent instanceof LoopContext) {
                this.loopContext = ((LoopContext) parent);
            } else {
                this.loopContext = parent.getLoopContext();
            }
        }
    }


    @Override
    public String toString() {
        return """
                {"id": "%s", "name": "%s", "node": %s, "metadata": {}, "activeParentId": "%s", "activeChildId": "%s"}
                """.formatted(
                id,
                name,
                node == null ? null : node.toString(),
                activeParent == null ? "" : activeParent.getId(),
                activeChild == null ? "" : activeChild.getId());
    }

    @Override
    public void updateStatus(State state) {
        this.node.updateStatus(Status.builder().state(state).build());
    }
}
