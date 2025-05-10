package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.State;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.node.*;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractBranchesNode;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractChainNode;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.event.Event;
import io.github.pheonixhkbxoic.adk.event.EventService;
import io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener;
import io.github.pheonixhkbxoic.adk.exception.AdkException;
import io.github.pheonixhkbxoic.adk.exception.PlainEdgeFallbackCountCheckException;
import io.github.pheonixhkbxoic.adk.session.Session;
import io.github.pheonixhkbxoic.adk.session.SessionService;
import lombok.Getter;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/6 06:23
 * @desc
 */
public class Executor {
    @Getter
    private final SessionService sessionService;
    private final EventService eventService;
    private transient ExecutorService es;

    public Executor(SessionService sessionService, EventService eventService) {
        this.sessionService = sessionService;
        this.eventService = eventService;
        this.eventService.addEventListener(new LogInvokeEventListener());
    }

    public AdkContext execute(Graph graph, RootContext rootContext) {
        String appName = graph.getName();
        Payload payload = rootContext.getPayload();
        String userId = payload.getUserId();
        String sessionId = payload.getSessionId();
        Session session;
        try {
            session = this.sessionService.addSession(appName, userId, sessionId, new Session(sessionId));
        } catch (Throwable e) {
            throw new AdkException("operate session exception", e);
        }

        String taskId = payload.getTaskId();
        session.updateSession(taskId, rootContext);

        Node curr = graph.getStart();
        AdkContext currContext = curr.buildContextFromParent(rootContext);
        return this.execute(appName, session, currContext, null);
    }

    private AdkContext execute(String appName, Session session, AdkContext currContext, CountDownLatch latchParallel) {
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
                session.updateSession(curr.getPayload().getTaskId(), curr);
                curr.updateStatus(State.of(State.EXECUTING));
                AdkContext context = this.doExecute(appName, session, curr, latchParallel);
                curr.updateStatus(State.of(State.SUCCESS));
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
                curr.updateStatus(State.of(State.FAILURE));
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

    private AdkContext doExecute(String appName, Session session, AdkContext currContext, CountDownLatch latchParallel) {
        AdkContext nextContext = null;
        Node curr = currContext.getNode();
        if (curr instanceof Group) {
            if (curr instanceof Loop) {
                nextContext = this.doExecuteLoopNode(appName, session, currContext, ((Loop) curr));
            }
        } else if (curr instanceof AbstractBranchesNode) {
            if (curr instanceof AgentRouter) {
                nextContext = this.doExecuteAgentRouterNode(currContext, ((AgentRouter) curr));
            } else if (curr instanceof Router) {
                nextContext = this.doExecuteRouterNode(currContext, ((Router) curr));
            } else if (curr instanceof Scatter) {
                nextContext = this.doExecuteScatterNode(appName, currContext, ((Scatter) curr));
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

    private AdkContext doExecuteLoopNode(String appName, Session session, AdkContext currContext, Loop curr) {
        LoopContext loopContext = (LoopContext) currContext;
        int epoch = 0;
        AdkContext parent = loopContext;
        while (((loopContext.getMaxEpoch() <= 0 || epoch < loopContext.getMaxEpoch()) && !loopContext.isBreaked())) {
            Node entry = curr.getEntry();
            loopContext.setEpoch(epoch);
            AdkContext entryContext = entry.buildContextFromParent(parent);
            // execute nodes in loop
            parent = this.execute(appName, session, entryContext, null);
            epoch++;
        }

        loopContext.setResponse(parent.getResponse());
        loopContext.setMetadata(parent.getMetadata());

        Node next = curr.getNext();
        return next.buildContextFromParent(loopContext);
    }

    private AdkContext doExecuteAgentRouterNode(AdkContext currContext, AgentRouter curr) {

        ExecutableContext agentContext = (ExecutableContext) currContext;
        Node agent = agentContext.getNode();
        // invoke agent
        Event eventAgentBefore = Event.builder()
                .type(Event.INVOKE)
                .nodeId(agent.getId())
                .nodeName(agent.getName())
                .stream(agentContext.getPayload().isStream())
                .build();
        eventService.send(eventAgentBefore);
        currContext.updateStatus(State.of(State.INVOKING));


        try {
            AdkAgentInvoker adkAgentInvoker = curr.getAdkAgentInvoker();
            adkAgentInvoker.beforeInvoke(agentContext);
            Flux<ResponseFrame> data;
            if (currContext.getPayload().isStream()) {
                data = adkAgentInvoker.invokeStream(agentContext);
            } else {
                data = adkAgentInvoker.invoke(agentContext).flux();
            }
            agentContext.setResponse(data);

            Event eventAfter = Event.builder()
                    .type(Event.INVOKE)
                    .nodeId(agent.getId())
                    .nodeName(agent.getName())
                    .stream(agentContext.getPayload().isStream())
                    .complete(true)
                    .build();
            eventService.send(eventAfter);
        } catch (Throwable e) {
            Event eventAfter = Event.builder()
                    .type(Event.INVOKE)
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
        currContext.updateStatus(State.of(State.ROUTING));


        try {
            List<Edge> edgeList = curr.getEdgeList();

            RouterContext routerContext = (RouterContext) currContext;
            BranchSelector selector = curr.getSelector();
            for (int i = 0; i < edgeList.size(); i++) {
                Edge edge = edgeList.get(i);
                boolean ok = selector.select(edge, i, edgeList.size(), routerContext);
                if (ok || (edge instanceof PlainEdge && ((PlainEdge) edge).isFallback())) {
                    routerContext.setSelectEdge(edge);
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
        currContext.updateStatus(State.of(State.ROUTING));


        try {
            List<Edge> edgeList = curr.getEdgeList();

            RouterContext routerContext = (RouterContext) currContext;
            BranchSelector selector = curr.getSelector();
            AdkContext nextContext = null;
            for (int i = 0; i < edgeList.size(); i++) {
                Edge edge = edgeList.get(i);
                boolean ok = selector.select(edge, i, edgeList.size(), routerContext);
                if (ok || (edge instanceof PlainEdge && ((PlainEdge) edge).isFallback())) {
                    routerContext.setSelectEdge(edge);
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

    private AdkContext doExecuteScatterNode(String appName, AdkContext currContext, Scatter curr) {
        List<Edge> edgeList = curr.getEdgeList();
        int parallelCount = curr.getEdgeList().size();
        if (es == null) {
            es = Executors.newCachedThreadPool();
        }
        try {
            Payload payload = currContext.getPayload();
            Session session = this.sessionService.getSession(appName, payload.getUserId(), payload.getSessionId());

            CountDownLatch latch = new CountDownLatch(parallelCount);
            List<Future<AdkContext>> parallelFutureList = edgeList.stream()
                    .map(edge -> {
                        final Node currParallel = edge.getNode();
                        AdkContext currContextParallel = currParallel.buildContextFromParent(currContext);
                        return es.submit(() -> this.execute(appName, session, currContextParallel, latch));
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
        if (curr instanceof Agent) {
            AgentContext agentContext = (AgentContext) currContext;
            Event eventBefore = Event.builder()
                    .type(Event.INVOKE)
                    .nodeId(curr.getId())
                    .nodeName(curr.getName())
                    .stream(agentContext.getPayload().isStream())
                    .build();
            eventService.send(eventBefore);
            currContext.updateStatus(State.of(State.INVOKING));

            try {
                AdkAgentInvoker adkAgentInvoker = ((Agent) curr).getAdkAgentInvoker();
                adkAgentInvoker.beforeInvoke(agentContext);
                Flux<ResponseFrame> data;
                if (agentContext.getPayload().isStream()) {
                    data = adkAgentInvoker.invokeStream(agentContext);
                } else {
                    data = adkAgentInvoker.invoke(agentContext).flux();
                }
                agentContext.setResponse(data);

                Event eventAfter = Event.builder()
                        .type(Event.INVOKE)
                        .nodeId(curr.getId())
                        .nodeName(curr.getName())
                        .stream(currContext.getPayload().isStream())
                        .complete(true)
                        .build();
                eventService.send(eventAfter);
            } catch (Throwable e) {
                Event eventAfter = Event.builder()
                        .type(Event.INVOKE)
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
