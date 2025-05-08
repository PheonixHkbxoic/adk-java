package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.node.Graph;
import io.github.pheonixhkbxoic.adk.event.InMemoryEventService;
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.Executor;
import io.github.pheonixhkbxoic.adk.runtime.ResponseFrame;
import io.github.pheonixhkbxoic.adk.runtime.RootContext;
import io.github.pheonixhkbxoic.adk.session.InMemorySessionService;
import io.github.pheonixhkbxoic.adk.uml.PlantUmlGenerator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 15:34
 * @desc
 */
@Getter
@Slf4j
public abstract class AbstractRunner implements Runner {
    protected final String appName;
    protected Executor executor;
    protected Graph graph;
    protected Consumer<Throwable> exceptionHandler;
    protected PlantUmlGenerator plantUmlGenerator;

    /**
     * 默认异常处理器，记录异常日志
     */
    public static Consumer<Throwable> DEFAULT_EXCEPTION_HANDLER = e -> log.info("exception: {}", e.getMessage(), e);


    protected AbstractRunner(String appName) {
        this.appName = appName;
        this.plantUmlGenerator = new PlantUmlGenerator();
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractRunner> T initExecutor(Executor executor) {
        this.executor = executor;
        return (T) this;
    }


    protected abstract Graph buildGraph();

    private void initDefault() {
        if (this.executor == null) {
            this.executor = new Executor(new InMemorySessionService(), new InMemoryEventService());
        }
        if (this.graph == null) {
            this.graph = this.buildGraph();
        }
    }

    @Override
    public ResponseFrame run(Payload payload) {
        RootContext rootContext = new RootContext(payload);
        this.initDefault();

        AdkContext ec = executor.execute(graph, rootContext);
        return ec.getResponse().blockFirst();
    }

    @Override
    public Flux<ResponseFrame> runAsync(Payload payload) {
        RootContext rootContext = new RootContext(payload);
        this.initDefault();

        AdkContext ec = executor.execute(graph, rootContext);
        return ec.getResponse()
                .doOnError(e -> {
                    if (exceptionHandler == null) {
                        exceptionHandler = DEFAULT_EXCEPTION_HANDLER;
                    }
                    exceptionHandler.accept(e);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }


    @Override
    public void setExceptionHandler(Consumer<Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

}
