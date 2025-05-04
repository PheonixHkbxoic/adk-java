package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.node.AgentNode;
import io.github.pheonixhkbxoic.adk.core.node.EndNode;
import io.github.pheonixhkbxoic.adk.core.node.StartNode;
import io.github.pheonixhkbxoic.adk.core.spec.GraphNode;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import io.github.pheonixhkbxoic.adk.runtime.ReadonlyContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

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
        EndNode endNode = EndNode.of();
        AgentNode agentNode = AgentNode.of("assistant", endNode);
        StartNode startNode = StartNode.of(agentNode);

        GraphNode graphNode = new GraphNode("assistant eva", startNode, endNode);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();
        ExecuteContext context = new ReadonlyContext(GraphNode.class.getSimpleName(), payload);
        ExecuteContext ec = graphNode.build(context).block();
        // ec is ReadonlyContext of end node
        assertThat(ec).extracting(ExecuteContext::getName).matches(s -> s.equals("endNode"));
    }


    @Test
    public void testBuildBranchGraph() {

    }


}
