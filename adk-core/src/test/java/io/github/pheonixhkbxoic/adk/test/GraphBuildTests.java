package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.edge.ConditionEdge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.node.*;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.ResponseFrame;
import io.github.pheonixhkbxoic.adk.runtime.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 01:28
 * @desc
 */
@Slf4j
public class GraphBuildTests {


    @Test
    public void testBuildChainGraph() {
        End end = End.of();
        Agentic agentNode = Agentic.of("assistant", end);
        Start start = Start.of(agentNode);

        Graph graphNode = new Graph("assistant eva", start, end);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();
        RootContext context = new RootContext(payload);
        ExecuteContext ec = graphNode.build(context).block();
        // ec is ReadonlyContext of end node
        assertThat(ec).extracting(ExecuteContext::getName).matches(s -> s.equals("endNode"));
    }


    @Test
    public void testBuildBranchGraph() {
        End end = End.of();
        Agentic agentNode01 = Agentic.of("assistant-01", end);
        Agentic agentNode02 = Agentic.of("assistant-02", end);

        // router and edges
        ConditionEdge branch01 = ConditionEdge.of("branch-01", (index, size, ec) -> {
            // mock
            return ec.getPayload().getUserId().contains("1");
        }, agentNode01);
        ConditionEdge branch02 = ConditionEdge.of("branch-02", (index, size, ec) -> {
            // mock
            return ec.getPayload().getUserId().contains("2");
        }, agentNode02);
        PlainEdge branchFallback = PlainEdge.of("branch-fallback", end, true);
        Router router = new Router("testRouter", List.of(branch01, branch02, branchFallback));

        Start start = Start.of(router);
        Graph graphNode = new Graph("assistant eva", start, end);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();
        RootContext context = new RootContext(payload);
        graphNode.build(context).subscribe(ec -> {
            Flux<ResponseFrame> frameFlux = ec.getResponseFrame();
            log.info("ec: {}", ec);
            if (frameFlux != null) {
                frameFlux.subscribe(responseFrame -> {
                    log.info("responseFrame: {}", responseFrame);
                });
            }
        });
    }


}
