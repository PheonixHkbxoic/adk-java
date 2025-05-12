package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.AdkAgentProvider;
import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.edge.DefaultRouterSelector;
import io.github.pheonixhkbxoic.adk.core.node.Graph;
import io.github.pheonixhkbxoic.adk.event.InMemoryEventService;
import io.github.pheonixhkbxoic.adk.runner.AgentLoopRunner;
import io.github.pheonixhkbxoic.adk.runner.AgentParallelRunner;
import io.github.pheonixhkbxoic.adk.runner.AgentRouterRunner;
import io.github.pheonixhkbxoic.adk.runner.AgentRunner;
import io.github.pheonixhkbxoic.adk.runtime.*;
import io.github.pheonixhkbxoic.adk.session.InMemorySessionService;
import io.github.pheonixhkbxoic.adk.uml.PlantUmlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 15:55
 * @desc
 */
@Slf4j
public class RunnerTests {

    private static Executor executor;

    @BeforeAll
    public static void init() {
        executor = new Executor(new InMemorySessionService(), new InMemoryEventService());
    }

    @Test
    public void testAgentRunner() {
        AdkAgentProvider qa = AdkAgentProvider.create("qaAssistant", new CustomAdkAgentInvoker());
        AgentRunner runner = AgentRunner.of("Assistant", qa).initExecutor(executor);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();

        // run
        ResponseFrame responseFrame = runner.run(payload);
        log.info("run responseFrame: {}", responseFrame);

    }

    @Test
    public void testAgentChainRunner() {
        String appName = "AgentChain";
        AdkAgentProvider qa = AdkAgentProvider.create("qaAssistant", new CustomAdkAgentInvoker());
        AdkAgentProvider qa2 = AdkAgentProvider.create("qaAssistant2", new CustomAdkAgentInvoker2());
        AgentRunner runner = AgentRunner.of(appName, qa, qa2).initExecutor(executor);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();

        // run
        ResponseFrame responseFrame = runner.run(payload);
        log.info("agent run responseFrame: {}", responseFrame);

        // gen uml png
        try {
            PlantUmlGenerator generator = runner.getPlantUmlGenerator();
            Graph graph = runner.getGraph();
            FileOutputStream file = new FileOutputStream("target/" + graph.getName() + ".png");
            generator.generatePng(graph, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // gen task uml png
        try {
            FileOutputStream file = new FileOutputStream("target/" + appName + "_" + payload.getTaskId() + ".png");
            runner.generateTaskPng(payload, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAgentChainRunnerAsync() {
        AdkAgentProvider qa = AdkAgentProvider.create("qaAssistant", new CustomAdkAgentInvoker());
        AdkAgentProvider qa2 = AdkAgentProvider.create("qaAssistant2", new CustomAdkAgentInvoker2());
        AgentRunner runner = AgentRunner.of("AgentChain", qa, qa2).initExecutor(executor);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").stream(true).build();

        // runAsync
        runner.runAsync(payload)
                .subscribe(responseFrame -> log.info("agent runAsync responseFrame: {}", responseFrame));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testAgentRouterRunner() {

        String routeFieldName = "activeAgent";
        AdkAgentProvider qaRouter = AdkAgentProvider.create("qaRouter", new AdkAgentInvoker() {
            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                // mock request llm and response
                Map<String, Object> metadata = Map.of(routeFieldName, "echoAgent", "answer", "router self answer..balabala...");
                context.setMetadata(metadata);
                return Mono.empty();
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                return this.invoke(context).flux();
            }
        });

        BranchSelector branchSelector = new DefaultRouterSelector(routeFieldName);

        AdkAgentProvider qa = AdkAgentProvider.create("echoAgent", new CustomAdkAgentInvoker());
        AdkAgentProvider qa2 = AdkAgentProvider.create("mathAgent", new CustomAdkAgentInvoker2());
        AdkAgentProvider fallback = AdkAgentProvider.create("fallback", new AdkAgentInvoker() {
            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                String answer = (String) context.getMetadata().get("answer");
                ResponseFrame response = new ResponseFrame();
                response.setMessage(answer);
                return Mono.just(response);
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                return this.invoke(context).flux();
            }
        });
        String appName = "AgentRouter";
        AgentRouterRunner runner = AgentRouterRunner.of(appName, qaRouter, branchSelector, fallback, qa, qa2)
                .initExecutor(executor);

        Payload payload = Payload.builder()
                .userId("1")
                .sessionId("2")
                .taskId(AdkUtil.uuid4hex())
                .message("hello")
                .stream(true)
                .build();

        // runAsync
        runner.runAsync(payload)
                .doFirst(() -> log.info("before runAsync"))
                .doOnError(e -> log.error("error runAsync: {}", e.getMessage(), e))
                .doOnComplete(() -> log.info("after runAsync"))
                .subscribe(responseFrame -> log.info("agent router runAsync responseFrame: {}", responseFrame));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<AdkContext> taskContextChain = runner.getTaskChainContextList(payload);
        log.info("taskContextChain: {}", taskContextChain);

        // gen uml png
        try {
            FileOutputStream file = new FileOutputStream("target/" + appName + ".png");
            runner.generatePng(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // gen task uml png
        try {
            FileOutputStream file = new FileOutputStream("target/" + appName + "_" + payload.getTaskId() + ".png");
            runner.generateTaskPng(payload, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAgentLoopRunner() {
        String appName = "AgentLoop";

        AdkAgentProvider first = AdkAgentProvider.create("qaAgent", new AdkAgentInvoker() {
            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                LoopContext loopContext = context.getLoopContext();
                if (loopContext != null) {
                    log.info("node in loop: {}, loop name: {}, loop epoch: {}, loop maxEpoch: {}",
                            context.getNode().getName(), loopContext.getName(), loopContext.getEpoch(), loopContext.getMaxEpoch());
                    String message = String.format("round %d %s: %s", loopContext.getEpoch() + 1, context.getName(), "message xxx");
                    return Mono.just(ResponseFrame.of(message));
                }
                return Mono.empty();
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                return this.invoke(context).flux();
            }
        });
        AdkAgentProvider second = AdkAgentProvider.create("evaluateAgent", new AdkAgentInvoker() {
            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                if (context.getMetadata() == null) {
                    context.setMetadata(new HashMap<>());
                }

                // evaluate qa score, exit if score > 80
                if (context.getLoopContext() != null) {
                    // mock evaluate score
                    double score = Double.parseDouble(context.getMetadata().getOrDefault("score", "0").toString());
                    score += 42;
                    context.getMetadata().put("score", score);
                    if (score > 80) {
                        context.getLoopContext().setBreaked(true);
                    }
                }
                return Mono.justOrEmpty(context.getResponse()
                        .map(r -> {
                            r.setMessage(r.getMessage() + " evaluated");
                            return r;
                        })
                        .blockFirst());
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                return this.invoke(context).flux();
            }
        });
        AgentLoopRunner runner = AgentLoopRunner.of(appName, 5, first, second)
                .initExecutor(executor);

        Payload payload = Payload.builder()
                .userId("1")
                .sessionId("2")
                .taskId(AdkUtil.uuid4hex())
                .message("hello")
                .build();

        // runAsync
        ResponseFrame responseFrame = runner.run(payload);
        log.info("loop runner responseFrame: {}", responseFrame.getMessage());

        try {
            FileOutputStream file = new FileOutputStream("target/" + appName + ".png");
            runner.generatePng(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            FileOutputStream file = new FileOutputStream("target/" + appName + "_" + payload.getTaskId() + ".png");
            runner.generateTaskPng(payload, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAgentParallelRunner() {
        String appName = "AgentParallel";
        AdkAgentProvider tasksGeneratorAgent = AdkAgentProvider.create("tasksGeneratorAgent", new AdkAgentInvoker() {
            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                return Mono.empty();
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                // mock agent generate tasks
                return Flux.create(sink -> {
                    for (int i = 0; i < 4; i++) {
                        sink.next(ResponseFrame.of(String.format("task-%02d", i)));
                    }
                    sink.complete();
                });
            }
        });

        AdkAgentProvider taskHandlerAgent = AdkAgentProvider.create("taskHandlerAgent", new AdkAgentInvoker() {
            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                return Mono.just(ResponseFrame.of("task handler message xxx"));
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                return this.invoke(context).flux();
            }
        });
        AgentParallelRunner runner = AgentParallelRunner.of(appName, tasksGeneratorAgent, taskHandlerAgent)
                .initExecutor(executor);

        Payload payload = Payload.builder()
                .userId("1")
                .sessionId("2")
                .taskId(AdkUtil.uuid4hex())
                .message("hello")
                .build();

        // runAsync
        ResponseFrame responseFrame = runner.run(payload);
        log.info("parallel runner responseFrame: {}", responseFrame.getMessage());

        try {
            FileOutputStream file = new FileOutputStream("target/" + appName + ".png");
            runner.generatePng(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            FileOutputStream file = new FileOutputStream("target/" + appName + "_" + payload.getTaskId() + ".png");
            runner.generateTaskPng(payload, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
