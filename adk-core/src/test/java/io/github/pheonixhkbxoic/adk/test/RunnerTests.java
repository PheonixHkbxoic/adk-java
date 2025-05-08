package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.AgentProvider;
import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.node.Graph;
import io.github.pheonixhkbxoic.adk.runner.AgentRouterRunner;
import io.github.pheonixhkbxoic.adk.runner.AgentRunner;
import io.github.pheonixhkbxoic.adk.runtime.AgentInvoker;
import io.github.pheonixhkbxoic.adk.runtime.BranchSelector;
import io.github.pheonixhkbxoic.adk.runtime.ExecutableContext;
import io.github.pheonixhkbxoic.adk.runtime.ResponseFrame;
import io.github.pheonixhkbxoic.adk.uml.PlantUmlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 15:55
 * @desc
 */
@Slf4j
public class RunnerTests {

    @Test
    public void testAgentRunner() {
        AgentProvider qa = AgentProvider.create("qaAssistant", new CustomAgentInvoker());
        AgentRunner runner = AgentRunner.of("assistant", qa);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();

        // run
        ResponseFrame responseFrame = runner.run(payload);
        log.info("run responseFrame: {}", responseFrame);

    }

    @Test
    public void testAgentRunner2() {
        AgentProvider qa = AgentProvider.create("qaAssistant", new CustomAgentInvoker());
        AgentProvider qa2 = AgentProvider.create("qaAssistant2", new CustomAgentInvoker2());
        AgentRunner runner = AgentRunner.of("assistant", qa, qa2);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();

        // run
        ResponseFrame responseFrame = runner.run(payload);
        log.info("agent run responseFrame: {}", responseFrame);

    }

    @Test
    public void testAgentRunner2Async() {
        AgentProvider qa = AgentProvider.create("qaAssistant", new CustomAgentInvoker());
        AgentProvider qa2 = AgentProvider.create("qaAssistant2", new CustomAgentInvoker2());
        AgentRunner runner = AgentRunner.of("assistant", qa, qa2);

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

        AgentProvider qaRouter = AgentProvider.create("qaRouter", new AgentInvoker() {
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

        AgentProvider qa = AgentProvider.create("echoAgent", new CustomAgentInvoker());
        AgentProvider qa2 = AgentProvider.create("mathAgent", new CustomAgentInvoker2());
        AgentProvider fallback = AgentProvider.create("fallback", new AgentInvoker() {
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
        AgentRouterRunner runner = AgentRouterRunner.of("assistant", qaRouter, branchSelector, fallback, qa, qa2);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").stream(true).build();

        // runAsync
        runner.runAsync(payload)
                .subscribe(responseFrame -> log.info("agentic router runAsync responseFrame: {}", responseFrame));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
