package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.AdkAgentProvider;
import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.node.Graph;
import io.github.pheonixhkbxoic.adk.event.InMemoryEventService;
import io.github.pheonixhkbxoic.adk.runner.AgentRouterRunner;
import io.github.pheonixhkbxoic.adk.runner.AgentRunner;
import io.github.pheonixhkbxoic.adk.runtime.*;
import io.github.pheonixhkbxoic.adk.session.InMemorySessionService;
import io.github.pheonixhkbxoic.adk.session.Session;
import io.github.pheonixhkbxoic.adk.uml.PlantUmlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
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
        AdkAgentProvider qa = AdkAgentProvider.create("qaAssistant", new CustomAdkAgentInvoker());
        AdkAgentProvider qa2 = AdkAgentProvider.create("qaAssistant2", new CustomAdkAgentInvoker2());
        AgentRunner runner = AgentRunner.of("AgentChain", qa, qa2).initExecutor(executor);

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
    public void testAgenticRouterRunner() {

        AdkAgentProvider qaRouter = AdkAgentProvider.create("qaRouter", new AdkAgentInvoker() {
            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                // mock request llm and response
                Map<String, Object> metadata = Map.of("activeAgent", "echoAgent", "answer", "router self answer..balabala...");
                context.setMetadata(metadata);
                return Mono.empty();
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                return this.invoke(context).flux();
            }
        });

        BranchSelector branchSelector = (edge, index, size, context) -> {
            Object activeAgent = context.getMetadata().get("activeAgent");
            return activeAgent != null && activeAgent.toString().equalsIgnoreCase(edge.getName());
        };

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
                .subscribe(responseFrame -> log.info("agentic router runAsync responseFrame: {}", responseFrame));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Session session = executor.getSessionService().getSession(appName, payload.getUserId(), payload.getSessionId());
        LinkedList<AdkContext> taskContextChain = session.getTaskContextChain(payload.getTaskId());
        log.info("taskContextChain: {}", taskContextChain);

        // gen uml png
        try {
            PlantUmlGenerator generator = runner.getPlantUmlGenerator();
            Graph graph = runner.getGraph();
            FileOutputStream file = new FileOutputStream("target/" + graph.getName() + ".png");
            generator.generatePng(graph, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
