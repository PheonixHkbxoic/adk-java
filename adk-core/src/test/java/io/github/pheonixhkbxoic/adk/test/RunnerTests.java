package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.Agent;
import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.runner.AgentRunner;
import io.github.pheonixhkbxoic.adk.runtime.ResponseFrame;
import io.github.pheonixhkbxoic.adk.session.InMemorySessionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

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
        InMemorySessionService sessionService = new InMemorySessionService();
        Agent qa = Agent.create("qaAssistant", new CustomAgentInvoker());
        AgentRunner runner = AgentRunner.create(sessionService, "assistant", qa);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();

        // run
        ResponseFrame responseFrame = runner.run(payload);
        log.info("run responseFrame: {}", responseFrame);

    }

    @Test
    public void testAgentRunnerAsync() {
        InMemorySessionService sessionService = new InMemorySessionService();
        Agent qa = Agent.create("qaAssistant", new CustomAgentInvoker());
        AgentRunner runner = AgentRunner.create(sessionService, "assistant", qa);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();

        // runAsync
        runner.runAsync(payload)
                .doFirst(() -> log.info("before runAsync"))
                .doOnComplete(() -> log.info("after runAsync"))
                .subscribe(responseFrame2 -> log.info("runAsync responseFrame: {}", responseFrame2));

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAgentRunnerOfAgentChain() {
        InMemorySessionService sessionService = new InMemorySessionService();
        Agent qa = Agent.create("qaAssistant", new CustomAgentInvoker());
        Agent qa2 = Agent.create("qaAssistant2", new CustomAgentInvoker());
        AgentRunner runner = AgentRunner.create(sessionService, "assistant", qa, qa2);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();

        // run
        ResponseFrame responseFrame = runner.run(payload);
        log.info("chain run responseFrame: {}", responseFrame);
    }

    @Test
    public void testAgentRunnerOfAgentChainAsync() {
        InMemorySessionService sessionService = new InMemorySessionService();
        Agent qa = Agent.create("qaAssistant", new CustomAgentInvoker());
        Agent qa2 = Agent.create("qaAssistant2", new CustomAgentInvoker());
        AgentRunner runner = AgentRunner.create(sessionService, "assistant", qa, qa2);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();


        // runAsync
        runner.runAsync(payload)
                .doFirst(() -> log.info("chain before runAsync"))
                .doOnComplete(() -> log.info("chain after runAsync"))
                .subscribe(responseFrame2 -> log.info("chain runAsync responseFrame: {}", responseFrame2));

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
