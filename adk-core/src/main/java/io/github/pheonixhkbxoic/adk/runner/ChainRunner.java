package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.Agent;
import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.node.AgentNode;
import io.github.pheonixhkbxoic.adk.core.node.EndNode;
import io.github.pheonixhkbxoic.adk.core.node.StartNode;
import io.github.pheonixhkbxoic.adk.core.spec.ChainNode;
import io.github.pheonixhkbxoic.adk.core.spec.GraphNode;
import io.github.pheonixhkbxoic.adk.event.EventListener;
import io.github.pheonixhkbxoic.adk.event.LogEventListener;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.ReadonlyContext;
import io.github.pheonixhkbxoic.adk.runtime.ResponseFrame;
import io.github.pheonixhkbxoic.adk.session.SessionService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 15:34
 * @desc
 */
@Slf4j
public abstract class ChainRunner implements Runner {
    /**
     * 默认异常处理器，记录异常日志
     */
    public static Consumer<Throwable> DEFAULT_EXCEPTION_HANDLER = e -> log.info("exception: {}", e.getMessage(), e);

    protected GraphNode graphNode;
    protected SessionService sessionService;
    protected Consumer<Throwable> exceptionHandler;
    protected List<EventListener> eventListenerList = new ArrayList<>();


    protected ChainRunner(SessionService sessionService, String appName, List<Agent> agents) {
        if (AdkUtil.isEmpty(appName)) {
            appName = GraphNode.class.getSimpleName();
        }
        this.sessionService = sessionService;
        EndNode endNode = EndNode.of();
        ChainNode chain = endNode;
        for (int i = agents.size() - 1; i >= 0; i--) {
            Agent agent = agents.get(i);
            chain = AgentNode.of(agent.getName(), agent.getAgentInvoker(), chain);
        }
        StartNode startNode = StartNode.of(chain);
        this.graphNode = new GraphNode(appName, startNode, endNode);
    }

    @Override
    public ResponseFrame run(Payload payload) {
        setDefaultEventLister();
        ExecuteContext rootContext = new ReadonlyContext("root", false, payload);
        rootContext.addEventListener(eventListenerList.toArray(EventListener[]::new));

        return graphNode
                // build
                .build(rootContext)
                // execute
                .flatMap(end -> graphNode.execute(rootContext.getChild()))
                .flux()
                .flatMap(ExecuteContext::getResponseFrame)
                .doOnError(e -> {
                    if (exceptionHandler == null) {
                        exceptionHandler = DEFAULT_EXCEPTION_HANDLER;
                    }
                    exceptionHandler.accept(e);
                })
                .blockFirst();
    }

    @Override
    public Flux<ResponseFrame> runAsync(Payload payload) {
        setDefaultEventLister();
        ExecuteContext rootContext = new ReadonlyContext("root", true, payload);
        rootContext.addEventListener(eventListenerList.toArray(EventListener[]::new));

        return graphNode
                // build
                .build(rootContext)
                // execute
                .publishOn(Schedulers.boundedElastic())
                .flatMap(end -> graphNode.execute(rootContext.getChild()))
                .flux()
                .flatMap(ExecuteContext::getResponseFrame)
                .doOnError(e -> {
                    if (exceptionHandler == null) {
                        exceptionHandler = DEFAULT_EXCEPTION_HANDLER;
                    }
                    exceptionHandler.accept(e);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Runner> T addEventListener(EventListener... eventListeners) {
        this.eventListenerList.addAll(List.of(eventListeners));
        return (T) this;
    }

    @Override
    public void setExceptionHandler(Consumer<Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    protected void setDefaultEventLister() {
        boolean logEventListenerExist = eventListenerList.stream().anyMatch(t -> t instanceof LogEventListener);
        if (!logEventListenerExist) {
            eventListenerList.add(0, new LogEventListener());
        }
    }
}
