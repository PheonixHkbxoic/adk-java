package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.Adk;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
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

    Payload getPayload();

    void setResponse(Flux<ResponseFrame> responseFrameFlux);

    Flux<ResponseFrame> getResponse();

    Map<String, Object> getMetadata();

    void setMetadata(Map<String, Object> metadata);

}
