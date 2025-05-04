package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.core.node.Agentic;
import io.github.pheonixhkbxoic.adk.core.node.End;
import io.github.pheonixhkbxoic.adk.core.node.Graph;
import io.github.pheonixhkbxoic.adk.core.node.Start;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractGraphNode;
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
        End end = End.of();
        Agentic agentNode = Agentic.of("assistant", end);
        Start start = Start.of(agentNode);

        Graph graphNode = new Graph("assistant eva", start, end);

        Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").build();
        ExecuteContext context = new ReadonlyContext(AbstractGraphNode.class.getSimpleName(), payload);
        ExecuteContext ec = graphNode.build(context).block();
        // ec is ReadonlyContext of end node
        assertThat(ec).extracting(ExecuteContext::getName).matches(s -> s.equals("endNode"));
    }


    @Test
    public void testBuildBranchGraph() {

    }


}
