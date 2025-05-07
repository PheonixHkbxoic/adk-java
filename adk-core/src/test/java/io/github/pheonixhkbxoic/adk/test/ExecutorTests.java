package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.edge.ConditionEdge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.node.*;
import io.github.pheonixhkbxoic.adk.event.InMemoryEventService;
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.Executor;
import io.github.pheonixhkbxoic.adk.runtime.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/6 16:34
 * @desc
 */
@Slf4j
public class ExecutorTests {

    InMemoryEventService es;
    Executor executor;

    @BeforeEach
    public void init() {
        es = new InMemoryEventService();
        executor = new Executor(es);
    }

    @Test
    public void test() {
        CustomAgentInvoker invoker01 = new CustomAgentInvoker();
        CustomAgentInvoker2 invoker02 = new CustomAgentInvoker2();
        End end = End.of();
        Agentic agentNode01 = Agentic.of("assistant-01", invoker01, end);
        Agentic agentNode02 = Agentic.of("assistant-02", invoker02, end);

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
        Graph graph = new Graph("assistant eva", start);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();
        RootContext rootContext = new RootContext(payload);
        AdkContext ec = executor.execute(graph, rootContext);
        log.info("context: {}", ec);
    }


}
