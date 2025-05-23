package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.context.AdkContext;
import io.github.pheonixhkbxoic.adk.context.RootContext;
import io.github.pheonixhkbxoic.adk.core.node.Agent;
import io.github.pheonixhkbxoic.adk.core.node.End;
import io.github.pheonixhkbxoic.adk.core.node.Graph;
import io.github.pheonixhkbxoic.adk.core.node.Start;
import io.github.pheonixhkbxoic.adk.event.InMemoryEventService;
import io.github.pheonixhkbxoic.adk.message.AdkPayload;
import io.github.pheonixhkbxoic.adk.message.ResponseFrame;
import io.github.pheonixhkbxoic.adk.runtime.Executor;
import io.github.pheonixhkbxoic.adk.session.InMemorySessionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/4 14:04
 * @desc
 */
@Slf4j
public class AdkAgentProviderNodeTests {
    Executor executor;

    @BeforeEach
    public void init() {
        this.executor = new Executor(new InMemorySessionService(), new InMemoryEventService());
    }

    @Test
    public void testAgentNode() {
        CustomAdkAgentInvoker invoker = new CustomAdkAgentInvoker();
        Agent agentNode = Agent.of(invoker, End.of());
        Graph graph = new Graph("assistant", Start.of(agentNode));
        RootContext rootCtx = new RootContext(AdkPayload.builder().build());
        AdkContext ec = executor.execute(graph, rootCtx);
        Flux<ResponseFrame> frames = ec.getResponse();
        assertThat(frames).isNotNull();
        frames.subscribe(frame -> log.info("frame: {}", frame.getMessage()));
    }


}
