package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.Adk;
import io.github.pheonixhkbxoic.adk.event.EventListener;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:13
 * @desc
 */
public interface ExecuteContext extends Adk {

    RootContext getRootContext();

    ExecuteContext getActiveParent();

    void setActiveParent(ExecuteContext parent);

    ExecuteContext getActiveChild();

    void setActiveChild(ExecuteContext child);

    Payload getPayload();

    boolean isAsync();

    void setResponseFrame(Flux<ResponseFrame> responseFrameFlux);

    Flux<ResponseFrame> getResponseFrame();

    void addEventListener(EventListener... eventListenerList);

    List<EventListener> getEventListenerList();
}
