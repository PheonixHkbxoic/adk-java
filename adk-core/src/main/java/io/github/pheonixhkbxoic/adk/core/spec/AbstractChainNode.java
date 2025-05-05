package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.event.Event;
import io.github.pheonixhkbxoic.adk.event.EventListener;
import io.github.pheonixhkbxoic.adk.runtime.AbstractBranchesExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:37
 * @desc
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Getter
@Setter
public abstract class AbstractChainNode extends AbstractNode {
    protected Edge edge;

    public AbstractChainNode(String name, String type, NodeInvoker invoker, Edge edge) {
        super(name, type, invoker);
        this.edge = edge;
    }

    @Override
    public Mono<ExecuteContext> build(ExecuteContext parentContext) {
        if (build) {
            return Mono.empty();
//            return Mono.just(parentContext.getRootContext().cache(this.getId()));
        }
        Mono<ExecuteContext> context = Mono.defer(() -> {

            ExecuteContext currContext = this.buildContextFromParent(parentContext);
            this.build = true;

            return Mono.justOrEmpty(this.edge)
                    .flatMap(e -> {
                        ExecuteContext block = e.getNode().build(currContext).block();
                        if (currContext.getActiveChild() == null) {
                            ExecuteContext child = currContext.getRootContext().cache(e.getNode().getId());
                            currContext.setActiveChild(child);
                            if (child instanceof AbstractBranchesExecuteContext) {
                                ((AbstractBranchesExecuteContext) child).addParent(currContext);
                            } else {
                                child.setActiveParent(currContext);
                            }
                            return Mono.just(child);
                        }
                        return Mono.justOrEmpty(block);
                    })
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
                    ExecuteContext parent = context.getActiveParent();
                    if (parent != null) {
                        context.setResponseFrame(parent.getResponseFrame());
                    }

                    // invoke
                    this.doInvoke(context);

                    return Mono.justOrEmpty(edge)
                            .flatMap(invokeContext -> edge.getNode().execute(context.getActiveChild()))
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


}
