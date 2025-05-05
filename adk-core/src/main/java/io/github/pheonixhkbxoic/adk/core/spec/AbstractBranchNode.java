package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.event.Event;
import io.github.pheonixhkbxoic.adk.event.EventListener;
import io.github.pheonixhkbxoic.adk.runtime.*;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:40
 * @desc
 */
@Getter
@Setter
public abstract class AbstractBranchNode extends AbstractNode {
    protected List<Edge> edgeList;

    public AbstractBranchNode(String name, String type, NodeInvoker nodeInvoker, List<Edge> edgeList) {
        super(name, type, nodeInvoker);
        this.edgeList = edgeList;
    }


    @Override
    public Mono<ExecuteContext> build(ExecuteContext parentContext) {
        if (build) {
            return Mono.empty();
//            return Mono.just(parentContext.getRootContext().cache(this.getId()));
        }
        Mono<ExecuteContext> context = Mono.defer(() -> {
            BranchesInvokeContext currContext = (BranchesInvokeContext) this.buildContextFromParent(parentContext);
            this.build = true;

            if (!edgeList.isEmpty()) {
                for (Edge edge : edgeList) {
                    int before = currContext.getChildList().size();
                    edge.getNode().build(currContext).subscribe();
                    int after = currContext.getChildList().size();
                    //
                    if (before == after) {
                        ExecuteContext cache = currContext.getRootContext().cache(edge.getNode().getId());
                        currContext.addChild(cache);
                        if (cache instanceof AbstractBranchesExecuteContext) {
                            ((AbstractBranchesExecuteContext) cache).addParent(currContext);
                        } else {
                            cache.setActiveParent(currContext);
                        }
                    }
                }
            }
            return Mono.just(currContext);
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

                    // select branch
                    AbstractBranchesExecuteContext currContext = (AbstractBranchesExecuteContext) context;
                    this.select(currContext)
                            .forEach(e -> {
                                Edge activeEdge = e.getKey();
                                ExecuteContext activeChild = e.getValue();
                                activeChild.setActiveParent(currContext);
                                currContext.setActiveChild(activeChild);

                                activeEdge.getNode().execute(currContext.getActiveChild());
                            });
                    return Mono.just(context);
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

    /**
     * select branches that match the condition of {@link BranchSelector#select(Edge, int, int, AbstractBranchesExecuteContext) } return true
     *
     * @return The branches that matches condition
     */
    abstract protected List<Map.Entry<Edge, ExecuteContext>> select(AbstractBranchesExecuteContext currContext);
}
