package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.node.Agentic;
import io.github.pheonixhkbxoic.adk.core.node.End;
import io.github.pheonixhkbxoic.adk.event.Event;
import io.github.pheonixhkbxoic.adk.event.EventListener;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.InvokeContext;
import io.github.pheonixhkbxoic.adk.runtime.ReadonlyContext;
import io.github.pheonixhkbxoic.adk.runtime.ResponseFrame;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/4 14:04
 * @desc
 */
@Slf4j
public class AgentNodeTests {

    @Test
    public void testInvoker() {
        List<EventListener> listeners = List.of();
        String nodeId = "agentNode-id", nodeName = "agentNode";
        CustomAgentInvoker invoker = new CustomAgentInvoker();
        InvokeContext invokeContext = new InvokeContext(nodeName, Payload.builder().build());
        Flux<ResponseFrame> flux = invoker.invokeStream(invokeContext)
                .doOnError(e -> {
                    Event eventAfter = Event.builder()
                            .type(Event.Invoke)
                            .nodeId(nodeId)
                            .nodeName(nodeName)
                            .stream(invokeContext.isAsync())
                            .complete(true)
                            .error(e)
                            .build();
                    AdkUtil.notifyInvokeEvent(listeners, eventAfter, true);
                })
                // TODO not work
                .doOnComplete(() -> {
                    Event eventAfter = Event.builder()
                            .type(Event.Invoke)
                            .nodeId(nodeId)
                            .nodeName(nodeName)
                            .stream(invokeContext.isAsync())
                            .complete(true)
                            .build();
                    AdkUtil.notifyInvokeEvent(listeners, eventAfter, true);
                });
        flux.subscribe(responseFrame -> log.info("responseFrame: {}", responseFrame));
    }

    @Test
    public void testAgentNode() {
        CustomAgentInvoker invoker = new CustomAgentInvoker();
        Agentic agentNode = Agentic.of(invoker, End.of());
        ReadonlyContext rootCtx = new ReadonlyContext("root", false, Payload.builder().build());
        ExecuteContext endCtx = agentNode.build(rootCtx).block();
        Mono<ExecuteContext> contextMono = agentNode.execute(rootCtx.getChild());
        contextMono.subscribe(ec -> {
            log.info("ec: {}", ec.getName());
            Flux<ResponseFrame> frames = ec.getResponseFrame();
            assertThat(frames).isNotNull();
            frames.subscribe(frame -> log.info("frame: {}", frame.getMessage()));
        });
    }

    @Test
    public void testAgentNodeAsync() {
        CustomAgentInvoker invoker = new CustomAgentInvoker();
        Agentic agentNode = Agentic.of(invoker, End.of());
        ReadonlyContext rootCtx = new ReadonlyContext("root", true, Payload.builder().build());
        ExecuteContext endCtx = agentNode.build(rootCtx).block();
        Mono<ExecuteContext> contextMono = agentNode.execute(rootCtx.getChild());
        contextMono.subscribe(ec -> {
            log.info("ec: {}", ec.getName());
            Flux<ResponseFrame> frames = ec.getResponseFrame();
            assertThat(frames).isNotNull();
            frames.subscribe(frame -> log.info("frame async: {}", frame.getMessage()));
        });
    }

}
