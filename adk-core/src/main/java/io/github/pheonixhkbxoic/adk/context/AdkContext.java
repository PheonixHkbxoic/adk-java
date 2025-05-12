package io.github.pheonixhkbxoic.adk.context;

import io.github.pheonixhkbxoic.adk.core.Adk;
import io.github.pheonixhkbxoic.adk.core.State;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.message.AdkPayload;
import io.github.pheonixhkbxoic.adk.message.ResponseFrame;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:13
 * @desc
 */
public interface AdkContext extends Adk {
    Node getNode();

    void setNode(Node node);

    AdkContext getActiveParent();

    void setActiveParent(AdkContext parent);

    AdkContext getActiveChild();

    void setActiveChild(AdkContext child);

    /**
     * get LoopContext
     *
     * @return return LoopContext if in Loop node, otherwise return null
     */
    LoopContext getLoopContext();

    AdkPayload getPayload();

    void setResponse(Flux<ResponseFrame> responseFrameFlux);

    Flux<ResponseFrame> getResponse();

    Map<String, Object> getMetadata();

    void setMetadata(Map<String, Object> metadata);

    void updateStatus(State state);
}
