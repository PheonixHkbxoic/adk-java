package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.event.EventListener;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 17:24
 * @desc
 */
@Getter
public class AbstractExecuteContext implements ExecuteContext {
    protected String id;
    protected String name;
    @Setter
    protected ExecuteContext parent;
    @Setter
    protected ExecuteContext child;
    protected Payload payload;
    protected boolean async;
    protected Flux<ResponseFrame> responseFrameFlux;
    protected List<EventListener> eventListenerList = new ArrayList<>();

    public AbstractExecuteContext(String name, boolean async, Payload payload) {
        this.id = AdkUtil.uuid4hex();
        this.name = name;
        this.async = async;
        this.payload = payload;
    }


    @Override
    public void addEventListener(EventListener... eventListenerList) {
        if (eventListenerList == null) {
            return;
        }
        this.eventListenerList.addAll(Arrays.asList(eventListenerList));
    }

    @Override
    public Flux<ResponseFrame> getResponseFrame() {
        return this.responseFrameFlux;
    }

    @Override
    public void setResponseFrame(Flux<ResponseFrame> responseFrameFlux) {
        this.responseFrameFlux = responseFrameFlux;
    }

    @Override
    public String toString() {
        return this.name + "@" + this.getClass().getSimpleName();
    }

    public String toStringChain() {
        StringBuilder sb = new StringBuilder();
        ExecuteContext curr = this;
        while (curr != null) {
            sb.append(String.format("%s(%s)", curr.getName(), curr.getClass().getSimpleName()));
            if (curr.getParent() != null) {
                sb.append(" -> ");
            }
            curr = curr.getParent();
        }
        return sb.toString();
    }

}
