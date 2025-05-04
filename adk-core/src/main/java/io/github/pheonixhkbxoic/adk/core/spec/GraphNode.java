package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.node.EndNode;
import io.github.pheonixhkbxoic.adk.core.node.StartNode;
import io.github.pheonixhkbxoic.adk.event.Event;
import io.github.pheonixhkbxoic.adk.event.EventListener;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:51
 * @desc
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Getter
@Setter
public class GraphNode extends AbstractNode {
    protected StartNode startNode;
    protected EndNode endNode;
    protected GraphNode next;

    public GraphNode(StartNode startNode, EndNode endNode) {
        this(null, startNode, endNode, null);
    }

    public GraphNode(String name, StartNode startNode, EndNode endNode) {
        this(name, startNode, endNode, null);
    }

    public GraphNode(String name, StartNode startNode, EndNode endNode, GraphNode next) {
        super(name, NodeType.GRAPH);
        this.startNode = startNode;
        this.endNode = endNode;
        this.next = next;
    }

    @Override
    public Mono<ExecuteContext> build(ExecuteContext parentContext) {

        Mono<ExecuteContext> endContextMono = Mono.defer(() -> {
            ExecuteContext graphContext = this.buildContextFromParent(parentContext, this);
            return startNode.build(graphContext);
        });

        if (next != null) {
            final Mono<ExecuteContext> prevMono = endContextMono;
            endContextMono = Mono.create(sink -> prevMono.subscribe(prevContext -> {
                Mono<ExecuteContext> contextMono = next.build(prevContext);
                contextMono.subscribe(sink::success);
            }));
        }

        List<EventListener> listeners = parentContext.getEventListenerList();
        return endContextMono
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
                .doOnSuccess(context -> {
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
                    ExecuteContext child = context.getChild();
                    return startNode.execute(child);
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
                    Event eventBefore = Event.builder()
                            .type(Event.Execute)
                            .nodeId(this.getId())
                            .nodeName(this.getName())
                            .stream(context.isAsync())
                            .complete(true)
                            .error(e)
                            .build();
                    AdkUtil.notifyExecuteEvent(listeners, eventBefore, true);
                })
                .doOnSuccess(end -> {
                    Event eventBefore = Event.builder()
                            .type(Event.Execute)
                            .nodeId(this.getId())
                            .nodeName(this.getName())
                            .stream(context.isAsync())
                            .complete(true)
                            .build();
                    AdkUtil.notifyExecuteEvent(listeners, eventBefore, true);
                });
    }

}
