package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.event.Event;
import io.github.pheonixhkbxoic.adk.event.EventListener;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.InvokeContext;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;
import io.github.pheonixhkbxoic.adk.runtime.ResponseFrame;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:37
 * @desc
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public abstract class AbstractChainNode extends AbstractNode {
    protected Edge edge;

    public AbstractChainNode(String name, String type, NodeInvoker invoker, Edge edge) {
        super(name, type, invoker);
        this.edge = edge;
    }

    @Override
    public Mono<ExecuteContext> build(ExecuteContext parentContext) {
        Mono<ExecuteContext> context = Mono.defer(() -> {
            ExecuteContext currContext = this.buildContextFromParent(parentContext, this);
            return Mono.justOrEmpty(this.edge)
                    .flatMap(e -> e.getNode().build(currContext))
                    .switchIfEmpty(Mono.just(currContext));
        });


        List<EventListener> listeners = parentContext.getEventListenerList();
        return context
                .doFirst(() -> {
                    Event eventBefore = Event.builder()
                            .type(Event.BUILD)
                            .nodeId(this.getId())
                            .nodeName(this.getName())
                            .stream(parentContext.isAsync())
                            .build();
                    AdkUtil.notifyBuildEvent(listeners, eventBefore, false);
                })
                .doOnError(e -> {
                    Event eventBefore = Event.builder()
                            .type(Event.BUILD)
                            .nodeId(this.getId())
                            .nodeName(this.getName())
                            .stream(parentContext.isAsync())
                            .complete(true)
                            .error(e)
                            .build();
                    AdkUtil.notifyBuildEvent(listeners, eventBefore, true);
                })
                .doOnSuccess(end -> {
                    Event eventBefore = Event.builder()
                            .type(Event.BUILD)
                            .nodeId(this.getId())
                            .nodeName(this.getName())
                            .stream(parentContext.isAsync())
                            .complete(true)
                            .build();
                    AdkUtil.notifyBuildEvent(listeners, eventBefore, true);
                });

    }

    @Override
    public Mono<ExecuteContext> execute(ExecuteContext context) {
        List<EventListener> listeners = context.getEventListenerList();
        return Mono.defer(() -> {
                    // transmit parent response
                    ExecuteContext parent = context.getParent();
                    if (parent != null) {
                        context.setResponseFrame(parent.getResponseFrame());
                    }

                    // invoke
                    this.doInvoke(context);

                    return Mono.justOrEmpty(edge)
                            .flatMap(invokeContext -> edge.getNode().execute(context.getChild()))
                            .switchIfEmpty(Mono.just(context));
                })
                .doFirst(() -> {
                    Event eventBefore = Event.builder()
                            .type(Event.Execute)
                            .nodeId(this.getId())
                            .nodeName(this.getName())
                            .stream(context.isAsync())
                            .build();
                    AdkUtil.notifyExecuteEvent(listeners, eventBefore, false);
                })
                .doOnError(e -> {
                    Event eventAfter = Event.builder()
                            .type(Event.Execute)
                            .nodeId(this.getId())
                            .nodeName(this.getName())
                            .stream(context.isAsync())
                            .complete(true)
                            .error(e)
                            .build();
                    AdkUtil.notifyExecuteEvent(listeners, eventAfter, true);
                })
                .doOnSuccess(end -> {
                    Event eventAfter = Event.builder()
                            .type(Event.Execute)
                            .nodeId(this.getId())
                            .nodeName(this.getName())
                            .stream(context.isAsync())
                            .complete(true)
                            .build();
                    AdkUtil.notifyExecuteEvent(listeners, eventAfter, true);
                });
    }

    private void doInvoke(ExecuteContext context) {
        if (this.invoker == null) {
            return;
        }
        InvokeContext invokeContext = (InvokeContext) context;
        List<EventListener> listeners = invokeContext.getEventListenerList();

        Flux<ResponseFrame> flux = Flux.defer(() -> {
                    // invoke
                    Flux<ResponseFrame> responseFrameFlux;
                    if (context.isAsync()) {
                        responseFrameFlux = invoker.invokeStream(invokeContext);
                    } else {
                        responseFrameFlux = invoker.invoke(invokeContext).flux();
                    }
                    return responseFrameFlux;
                })
                .doFirst(() -> {
                    Event eventBefore = Event.builder()
                            .type(Event.Invoke)
                            .nodeId(this.getId())
                            .nodeName(this.getName())
                            .stream(invokeContext.isAsync())
                            .build();
                    AdkUtil.notifyInvokeEvent(listeners, eventBefore, false);
                })
                .doOnError(e -> {
                    Event eventAfter = Event.builder()
                            .type(Event.Invoke)
                            .nodeId(this.getId())
                            .nodeName(this.getName())
                            .stream(invokeContext.isAsync())
                            .complete(true)
                            .error(e)
                            .build();
                    AdkUtil.notifyInvokeEvent(listeners, eventAfter, true);
                })
                .doOnComplete(() -> {
                    Event eventAfter = Event.builder()
                            .type(Event.Invoke)
                            .nodeId(this.getId())
                            .nodeName(this.getName())
                            .stream(invokeContext.isAsync())
                            .complete(true)
                            .build();
                    AdkUtil.notifyInvokeEvent(listeners, eventAfter, true);
                });
        flux = Flux.fromStream(flux.toStream());
        invokeContext.setResponseFrame(flux);
    }

}
