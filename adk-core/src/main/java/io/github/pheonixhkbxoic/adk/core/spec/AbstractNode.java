package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.core.State;
import io.github.pheonixhkbxoic.adk.core.Status;
import io.github.pheonixhkbxoic.adk.core.node.Agentic;
import io.github.pheonixhkbxoic.adk.event.EventListener;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.InvokeContext;
import io.github.pheonixhkbxoic.adk.runtime.NodeInvoker;
import io.github.pheonixhkbxoic.adk.runtime.ReadonlyContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

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


    protected ExecuteContext buildContextFromParent(ExecuteContext parent, Node currNode) {
        if (currNode instanceof Agentic) {
            InvokeContext context = new InvokeContext(currNode.getName(), parent.isAsync(), parent.getPayload());
            context.setParent(parent);
            parent.setChild(context);
            context.setResponseFrame(parent.getResponseFrame());
            context.addEventListener(parent.getEventListenerList().toArray(new EventListener[0]));
            return context;
        }

        // chain,branch,graph
        ReadonlyContext context = new ReadonlyContext(currNode.getName(), parent.isAsync(), parent.getPayload());
        parent.setChild(context);
        context.setParent(parent);
        context.setResponseFrame(parent.getResponseFrame());
        context.addEventListener(parent.getEventListenerList().toArray(new EventListener[0]));
        return context;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
