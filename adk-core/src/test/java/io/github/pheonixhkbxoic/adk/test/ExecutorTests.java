package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.context.AdkContext;
import io.github.pheonixhkbxoic.adk.context.RootContext;
import io.github.pheonixhkbxoic.adk.core.edge.ConditionEdge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.node.*;
import io.github.pheonixhkbxoic.adk.event.InMemoryEventService;
import io.github.pheonixhkbxoic.adk.message.AdkPayload;
import io.github.pheonixhkbxoic.adk.message.AdkTextMessage;
import io.github.pheonixhkbxoic.adk.runtime.Executor;
import io.github.pheonixhkbxoic.adk.session.InMemorySessionService;
import io.github.pheonixhkbxoic.adk.session.Session;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/6 16:34
 * @desc
 */
@Slf4j
public class ExecutorTests {

    private Executor executor;

    @BeforeEach
    public void init() {
        this.executor = new Executor(new InMemorySessionService(), new InMemoryEventService());
    }

    @Test
    public void test() {
        CustomAdkAgentInvoker invoker01 = new CustomAdkAgentInvoker();
        CustomAdkAgentInvoker2 invoker02 = new CustomAdkAgentInvoker2();
        End end = End.of();
        Agent agentNode01 = Agent.of("assistant-01", invoker01, end);
        Agent agentNode02 = Agent.of("assistant-02", invoker02, end);

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
        String appName = "AgentRouter";
        Graph graph = new Graph(appName, start);

        AdkPayload payload = AdkPayload.builder()
                .userId("1")
                .sessionId("2")
                .taskId(AdkUtil.uuid4hex())
                .messages(List.of(AdkTextMessage.of("hello")))
                .build();
        RootContext rootContext = new RootContext(payload);
        AdkContext ec = executor.execute(graph, rootContext);
        log.info("context: {}", ec);
        Session session = executor.getSessionService().getSession(appName, payload.getUserId(), payload.getSessionId());
        LinkedList<AdkContext> taskContextChain = session.getTaskContextChain(rootContext.getPayload().getTaskId());
        log.info("taskContextChain: {}", taskContextChain);
    }


}
