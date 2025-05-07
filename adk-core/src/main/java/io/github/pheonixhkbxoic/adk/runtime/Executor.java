package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.node.*;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractBranchesNode;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractChainNode;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.event.Event;
import io.github.pheonixhkbxoic.adk.event.EventService;
import io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener;
import io.github.pheonixhkbxoic.adk.exception.PlainEdgeFallbackCountCheckException;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/6 06:23
 * @desc
 */
public class Executor {
    private final EventService eventService;
    private ExecutorService es;

    public Executor(EventService eventService) {
        this.eventService = eventService;
        this.eventService.addEventListener(new LogInvokeEventListener());
    }

    public AdkContext execute(Graph graph, RootContext rootContext) {
        Node curr = graph.getStart();
        AdkContext currContext = curr.buildContextFromParent(rootContext);
        return this.execute(currContext, null);
    }

    private AdkContext execute(AdkContext currContext, CountDownLatch latchParallel) {
        AdkContext last = currContext;
        AdkContext curr = currContext;
        while (curr != null) {
            Node eventCurr = curr.getNode();
            AdkContext eventCurrContext = curr;

            Event eventBefore = Event.builder()
                    .type(Event.Execute)
                    .nodeId(eventCurr.getId())
                    .nodeName(eventCurr.getName())
                    .stream(eventCurrContext.getPayload().isStream())
                    .build();
            eventService.send(eventBefore);


            try {
                // execute and go next context
                AdkContext context = this.doExecute(curr, latchParallel);
                if (context != null) {
                    last = context;
                }
                curr = context;

                Event eventAfter = Event.builder()
                        .type(Event.Execute)
                        .nodeId(eventCurr.getId())
                        .nodeName(eventCurr.getName())
                        .stream(eventCurrContext.getPayload().isStream())
                        .complete(true)
                        .build();
                eventService.send(eventAfter);
            } catch (Throwable e) {
                Event eventAfter = Event.builder()
                        .type(Event.Execute)
                        .nodeId(eventCurr.getId())
                        .nodeName(eventCurr.getName())
                        .stream(eventCurrContext.getPayload().isStream())
                        .complete(true)
                        .error(e)
                        .build();
                eventService.send(eventAfter);
                throw e;
            }
        }
        return last;
    }

    private AdkContext doExecute(AdkContext currContext, CountDownLatch latchParallel) {
        AdkContext nextContext = null;
        Node curr = currContext.getNode();
        if (curr instanceof AbstractBranchesNode) {
            if (curr instanceof AgenticRouter) {
                nextContext = this.doExecuteAgenticRouterNode(currContext, ((AgenticRouter) curr));
            } else if (curr instanceof Router) {
                nextContext = this.doExecuteRouterNode(currContext, ((Router) curr));
            } else if (curr instanceof Scatter) {
                nextContext = this.doExecuteScatterNode(currContext, ((Scatter) curr));
            }
        } else if (curr instanceof Aggregator) {
            if (latchParallel != null) {
                latchParallel.countDown();
            } else {
                Node next = ((Aggregator) curr).getEdge().getNode();
                nextContext = next.buildContextFromParent(currContext);
            }
        } else if (curr instanceof AbstractChainNode) {
            nextContext = this.doExecuteChainNode(currContext, ((AbstractChainNode) curr));
        } else {
            //
            throw new RuntimeException("unknown node type" + curr.getClass().getName());
        }
        return nextContext;
    }

    private AdkContext doExecuteAgenticRouterNode(AdkContext currContext, AgenticRouter curr) {

        ExecutableContext agentContext = (ExecutableContext) currContext;
        Node agent = agentContext.getNode();
        // invoke agentic
        Event eventAgentBefore = Event.builder()
                .type(Event.AGENT_INVOKE)
                .nodeId(agent.getId())
                .nodeName(agent.getName())
                .stream(agentContext.getPayload().isStream())
                .build();
        eventService.send(eventAgentBefore);


        try {
            AgentInvoker agentInvoker = curr.getAgentInvoker();
            agentInvoker.beforeInvoke(agentContext);
            Flux<ResponseFrame> data;
            if (currContext.getPayload().isStream()) {
                data = agentInvoker.invokeStream(agentContext);
            } else {
                data = agentInvoker.invoke(agentContext).flux();
            }
            agentContext.setResponse(data);

            Event eventAfter = Event.builder()
                    .type(Event.AGENT_INVOKE)
                    .nodeId(agent.getId())
                    .nodeName(agent.getName())
                    .stream(agentContext.getPayload().isStream())
                    .complete(true)
                    .build();
            eventService.send(eventAfter);
        } catch (Throwable e) {
            Event eventAfter = Event.builder()
                    .type(Event.AGENT_INVOKE)
                    .nodeId(agent.getId())
                    .nodeName(agent.getName())
                    .stream(agentContext.getPayload().isStream())
                    .complete(true)
                    .error(e)
                    .build();
            eventService.send(eventAfter);
            throw e;
        }

        AdkContext nextContext = null;
        // route
        Event eventBefore = Event.builder()
                .type(Event.ROUTE)
                .nodeId(curr.getId())
                .nodeName(curr.getName())
                .stream(currContext.getPayload().isStream())
                .build();
        eventService.send(eventBefore);


        try {
            List<Edge> edgeList = curr.getEdgeList();

            BranchSelector selector = curr.getSelector();
            for (int i = 0; i < edgeList.size(); i++) {
                Edge edge = edgeList.get(i);
                ExecutableContext routerContext = (ExecutableContext) currContext;
                boolean ok = selector.select(edge, i, edgeList.size(), routerContext);
                if (ok || (edge instanceof PlainEdge && ((PlainEdge) edge).isFallback())) {
                    Node next = edge.getNode();
                    nextContext = next.buildContextFromParent(currContext);
                    break;
                }
            }
            if (nextContext == null) {
                // should not arrive here
                throw new PlainEdgeFallbackCountCheckException("not found fallback PlainEdge in Router: " + curr.getName());
            }

            Event eventAfter = Event.builder()
                    .type(Event.ROUTE)
                    .nodeId(curr.getId())
                    .nodeName(curr.getName())
                    .stream(currContext.getPayload().isStream())
                    .complete(true)
                    .build();
            eventService.send(eventAfter);
        } catch (Throwable e) {
            Event eventAfter = Event.builder()
                    .type(Event.ROUTE)
                    .nodeId(curr.getId())
                    .nodeName(curr.getName())
                    .stream(currContext.getPayload().isStream())
                    .complete(true)
                    .error(e)
                    .build();
            eventService.send(eventAfter);
            throw e;
        }


        return nextContext;
    }

    private AdkContext doExecuteRouterNode(AdkContext currContext, Router curr) {
        Event eventBefore = Event.builder()
                .type(Event.ROUTE)
                .nodeId(curr.getId())
                .nodeName(curr.getName())
                .stream(currContext.getPayload().isStream())
                .build();
        eventService.send(eventBefore);


        try {
            List<Edge> edgeList = curr.getEdgeList();

            BranchSelector selector = curr.getSelector();
            AdkContext nextContext = null;
            for (int i = 0; i < edgeList.size(); i++) {
                Edge edge = edgeList.get(i);
                assert currContext instanceof RouterContext;
                RouterContext routerContext = (RouterContext) currContext;
                boolean ok = selector.select(edge, i, edgeList.size(), routerContext);
                if (ok || (edge instanceof PlainEdge && ((PlainEdge) edge).isFallback())) {
                    Node next = edge.getNode();
                    nextContext = next.buildContextFromParent(currContext);
                    break;
                }
            }
            if (nextContext == null) {
                // should not arrive here
                throw new PlainEdgeFallbackCountCheckException("not found fallback PlainEdge in Router: " + curr.getName());
            }

            Event eventAfter = Event.builder()
                    .type(Event.ROUTE)
                    .nodeId(curr.getId())
                    .nodeName(curr.getName())
                    .stream(currContext.getPayload().isStream())
                    .complete(true)
                    .build();
            eventService.send(eventAfter);
            return nextContext;
        } catch (Throwable e) {
            Event eventAfter = Event.builder()
                    .type(Event.ROUTE)
                    .nodeId(curr.getId())
                    .nodeName(curr.getName())
                    .stream(currContext.getPayload().isStream())
                    .complete(true)
                    .error(e)
                    .build();
            eventService.send(eventAfter);
            throw e;
        }
    }

    private AdkContext doExecuteScatterNode(AdkContext currContext, Scatter curr) {
        List<Edge> edgeList = curr.getEdgeList();
        int parallelCount = curr.getEdgeList().size();
        if (es == null) {
            es = Executors.newCachedThreadPool();
        }
        try {
            CountDownLatch latch = new CountDownLatch(parallelCount);
            List<Future<AdkContext>> parallelFutureList = edgeList.stream()
                    .map(edge -> {
                        final Node currParallel = edge.getNode();
                        AdkContext currContextParallel = currParallel.buildContextFromParent(currContext);
                        return es.submit(() -> this.execute(currContextParallel, latch));
                    })
                    .toList();
            // await all thread arrive Aggregator
            latch.await();

            // check
            List<AdkContext> resultParallel = parallelFutureList.stream().map(ec -> {
                try {
                    return ec.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
            long count = resultParallel.stream().map(AdkContext::getId).distinct().count();
            if (count != 1) {
                throw new RuntimeException("All of scatter branches should end with aggregator");
            }
            // goto aggregator
            AdkContext nextContext = resultParallel.get(0);
            // TODO resultParallel merge


            return nextContext;
        } catch (InterruptedException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private AdkContext doExecuteChainNode(AdkContext currContext, AbstractChainNode curr) {
        AdkContext nextContext = null;
        if (curr instanceof Agentic) {
            AgentContext agentContext = (AgentContext) currContext;
            Event eventBefore = Event.builder()
                    .type(Event.AGENT_INVOKE)
                    .nodeId(curr.getId())
                    .nodeName(curr.getName())
                    .stream(agentContext.getPayload().isStream())
                    .build();
            eventService.send(eventBefore);


            try {
                AgentInvoker agentInvoker = ((Agentic) curr).getAgentInvoker();
                agentInvoker.beforeInvoke(agentContext);
                Flux<ResponseFrame> data;
                if (agentContext.getPayload().isStream()) {
                    data = agentInvoker.invokeStream(agentContext);
                } else {
                    data = agentInvoker.invoke(agentContext).flux();
                }
                agentContext.setResponse(data);

                Event eventAfter = Event.builder()
                        .type(Event.AGENT_INVOKE)
                        .nodeId(curr.getId())
                        .nodeName(curr.getName())
                        .stream(currContext.getPayload().isStream())
                        .complete(true)
                        .build();
                eventService.send(eventAfter);
            } catch (Throwable e) {
                Event eventAfter = Event.builder()
                        .type(Event.AGENT_INVOKE)
                        .nodeId(curr.getId())
                        .nodeName(curr.getName())
                        .stream(currContext.getPayload().isStream())
                        .complete(true)
                        .error(e)
                        .build();
                eventService.send(eventAfter);
                throw e;
            }
        }

        Edge edge = curr.getEdge();
        if (edge != null) {
            Node next = edge.getNode();
            nextContext = next.buildContextFromParent(currContext);
        }
        return nextContext;
    }


}
