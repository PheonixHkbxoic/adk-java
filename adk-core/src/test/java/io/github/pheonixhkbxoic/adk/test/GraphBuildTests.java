package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.core.edge.ConditionEdge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.node.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        Agent agentNode = Agent.of("assistant", null, end);
        Start start = Start.of(agentNode);

        Graph graph = new Graph("assistant eva", start);
        log.info("chain graph: {}", graph);

    }


    @Test
    public void testBranchesGraph() {
        End end = End.of();
        Agent agentNode01 = Agent.of("assistant-01", null, end);
        Agent agentNode02 = Agent.of("assistant-02", null, end);

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
        log.info("branches graph: {}", graph);

    }


}
