package io.github.pheonixhkbxoic.adk.runtime;

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
 * @date 2025/5/4 23:14
 * @desc
 */
@Getter
@Setter
public abstract class AbstractBranchesExecuteContext extends AbstractExecuteContext {
    protected String id;
    protected String name;
    protected List<ExecuteContext> parentList;
    protected List<ExecuteContext> childList;
    protected Payload payload;
    protected boolean async;
    protected Flux<ResponseFrame> responseFrameFlux;
    protected List<EventListener> eventListenerList = new ArrayList<>();

    public AbstractBranchesExecuteContext(String id, String name, boolean async, Payload payload) {
        super(id, name, async, payload);
        this.parentList = new ArrayList<>();
        this.childList = new ArrayList<>();
    }

    public void addParent(ExecuteContext parent) {
        this.parentList.add(parent);
    }

    public void addChild(ExecuteContext child) {
        this.childList.add(child);
    }

    @Override
    public void addEventListener(EventListener... eventListenerList) {
        if (eventListenerList == null) {
            return;
        }
        this.eventListenerList.addAll(Arrays.asList(eventListenerList));
    }

    @Override
    public List<EventListener> getEventListenerList() {
        return List.of();
    }

    @Override
    public Flux<ResponseFrame> getResponseFrame() {
        return this.responseFrameFlux;
    }


    @Override
    public void setResponseFrame(Flux<ResponseFrame> responseFrameFlux) {
        this.responseFrameFlux = responseFrameFlux;
    }


}
