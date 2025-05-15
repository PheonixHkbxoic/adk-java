package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.context.AdkContext;
import io.github.pheonixhkbxoic.adk.context.RootContext;
import io.github.pheonixhkbxoic.adk.core.node.Graph;
import io.github.pheonixhkbxoic.adk.event.InMemoryEventService;
import io.github.pheonixhkbxoic.adk.message.AdkPayload;
import io.github.pheonixhkbxoic.adk.message.ResponseFrame;
import io.github.pheonixhkbxoic.adk.runtime.Executor;
import io.github.pheonixhkbxoic.adk.session.InMemorySessionService;
import io.github.pheonixhkbxoic.adk.session.Session;
import io.github.pheonixhkbxoic.adk.uml.PlantUmlGenerator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.FileFormat;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
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
    public static Consumer<Throwable> DEFAULT_EXCEPTION_HANDLER = e -> {
    };

    @Override
    public Consumer<Throwable> getExceptionHandler() {
        return Optional.ofNullable(exceptionHandler).orElse(DEFAULT_EXCEPTION_HANDLER);
    }

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
    public List<ResponseFrame> run(AdkPayload payload) {
        RootContext rootContext = new RootContext(payload);
        try {
            return this.doRun(rootContext)
                    .doOnError(e -> this.getExceptionHandler().accept(e))
                    .toStream()
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<ResponseFrame> run(AdkPayload payload, Consumer<Throwable> exceptionHandler) {
        RootContext rootContext = new RootContext(payload);
        try {
            return doRun(rootContext)
                    .doOnError(e -> Optional.ofNullable(exceptionHandler).orElse(this.getExceptionHandler()).accept(e))
                    .toStream()
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Flux<ResponseFrame> runAsync(AdkPayload payload) {
        RootContext rootContext = new RootContext(payload);
        return doRun(rootContext)
                .doOnError(e -> this.getExceptionHandler().accept(e))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Flux<ResponseFrame> doRun(RootContext rootContext) {
        return Flux.defer(() -> {
            this.initDefault();
            AdkContext adkContext = executor.execute(graph, rootContext);
            return adkContext.getResponse();
        });
    }


    @Override
    public void setExceptionHandler(Consumer<Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public List<AdkContext> getTaskChainContextList(AdkPayload payload) {
        Session session = executor.getSessionService().getSession(appName, payload.getUserId(), payload.getSessionId());
        return session.getTaskContextChain(payload.getTaskId());
    }

    @Override
    public void generate(OutputStream outputStream, FileFormat format) throws IOException {
        PlantUmlGenerator generator = this.getPlantUmlGenerator();
        generator.generate(graph, outputStream, format);
    }


    @Override
    public void generateTask(AdkPayload payload, OutputStream outputStream, FileFormat format) throws IOException {
        List<AdkContext> taskChainContextList = this.getTaskChainContextList(payload);
        this.getPlantUmlGenerator().generate(taskChainContextList, graph, outputStream, format);
    }
}
