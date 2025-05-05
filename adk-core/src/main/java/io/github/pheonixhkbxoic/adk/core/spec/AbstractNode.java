package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.core.State;
import io.github.pheonixhkbxoic.adk.core.Status;
import io.github.pheonixhkbxoic.adk.core.node.Agentic;
import io.github.pheonixhkbxoic.adk.event.Event;
import io.github.pheonixhkbxoic.adk.event.EventListener;
import io.github.pheonixhkbxoic.adk.runtime.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:17
 * @desc
 */
@Data
@Slf4j
public abstract class AbstractNode implements Node {
    protected String id;
    protected String name;
    protected String type;
    protected Status status;
    protected NodeInvoker invoker;
    protected boolean build;

    public AbstractNode() {
    }

    public AbstractNode(String type) {
        this(null, type, null);
    }

    public AbstractNode(String name, String type) {
        this(name, type, null);
    }

    public AbstractNode(String name, String type, NodeInvoker nodeInvoker) {
        this.id = AdkUtil.uuid4hex();
        this.name = name;
        if (AdkUtil.isEmpty(name)) {
            String simpleName = this.getClass().getSimpleName();
            this.name = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        }
        this.status = Status.builder().state(State.of(State.READY)).build();
        this.type = type;
        this.invoker = nodeInvoker;
    }

    @Override
    public Mono<ExecuteContext> build(ExecuteContext parent) {
        return Mono.error(new RuntimeException("The build method is unimplemented in " + this.getClass().getSimpleName()));
    }

    @Override
    public Mono<ExecuteContext> execute(ExecuteContext context) {
        return Mono.just(context);
    }


    protected ExecuteContext buildContextFromParent(ExecuteContext parent) {
        if (this instanceof AbstractBranchNode) {
            BranchesInvokeContext context = new BranchesInvokeContext(this.getId(), this.getName(), parent.isAsync(), parent.getPayload());
            if (parent instanceof AbstractBranchesExecuteContext) {
                ((AbstractBranchesExecuteContext) parent).addChild(context);
            } else {
                parent.setActiveChild(context);
            }
            context.addParent(parent);

            context.addEventListener(parent.getEventListenerList().toArray(new EventListener[0]));

            RootContext rootContext = parent instanceof RootContext ? ((RootContext) parent) : parent.getRootContext();
            rootContext.cache(this.getId(), context);
            context.setRootContext(rootContext);
            return context;
        }

        if (this instanceof Agentic) {
            InvokeContext context = new InvokeContext(this.getId(), this.getName(), parent.isAsync(), parent.getPayload());
            context.setActiveParent(parent);
            if (parent instanceof AbstractBranchesExecuteContext) {
                ((AbstractBranchesExecuteContext) parent).addChild(context);
            } else {
                parent.setActiveChild(context);
            }
            context.addEventListener(parent.getEventListenerList().toArray(new EventListener[0]));

            RootContext rootContext = parent instanceof RootContext ? ((RootContext) parent) : parent.getRootContext();
            rootContext.cache(this.getId(), context);
            context.setRootContext(rootContext);
            return context;
        }

        // chain
        ReadonlyContext context = new ReadonlyContext(this.getId(), this.getName(), parent.isAsync(), parent.getPayload());
        context.setActiveParent(parent);
        if (parent instanceof AbstractBranchesExecuteContext) {
            ((AbstractBranchesExecuteContext) parent).addChild(context);
        } else {
            parent.setActiveChild(context);
        }
        context.addEventListener(parent.getEventListenerList().toArray(new EventListener[0]));

        RootContext rootContext = parent instanceof RootContext ? ((RootContext) parent) : parent.getRootContext();
        rootContext.cache(this.getId(), context);
        context.setRootContext(rootContext);
        return context;
    }

    protected void doInvoke(ExecuteContext context) {
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

    @Override
    public String toString() {
        return this.name;
    }
}
