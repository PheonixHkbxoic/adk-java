package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.node.Agentic;
import io.github.pheonixhkbxoic.adk.core.node.End;
import io.github.pheonixhkbxoic.adk.core.node.Graph;
import io.github.pheonixhkbxoic.adk.core.node.Start;
import io.github.pheonixhkbxoic.adk.event.InMemoryEventService;
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.Executor;
import io.github.pheonixhkbxoic.adk.runtime.ResponseFrame;
import io.github.pheonixhkbxoic.adk.runtime.RootContext;
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
public class AgentNodeTests {
    Executor executor;

    @BeforeEach
    public void init() {
        this.executor = new Executor(new InMemoryEventService());
    }

    @Test
    public void testAgentNode() {
        CustomAgentInvoker invoker = new CustomAgentInvoker();
        Agentic agentNode = Agentic.of(invoker, End.of());
        Graph graph = new Graph("assistant", Start.of(agentNode));
        RootContext rootCtx = new RootContext(Payload.builder().build());
        AdkContext ec = executor.execute(graph, rootCtx);
        Flux<ResponseFrame> frames = ec.getResponse();
        assertThat(frames).isNotNull();
        frames.subscribe(frame -> log.info("frame: {}", frame.getMessage()));
    }


}
