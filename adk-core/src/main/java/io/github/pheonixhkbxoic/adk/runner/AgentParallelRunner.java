package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.core.AdkAgentProvider;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.node.*;
import io.github.pheonixhkbxoic.adk.runtime.AggregatorDataMerger;
import io.github.pheonixhkbxoic.adk.runtime.AggregatorNextPredicator;
import io.github.pheonixhkbxoic.adk.runtime.DefaultAggregatorDataMerger;
import io.github.pheonixhkbxoic.adk.runtime.DefaultAggregatorNextPredicator;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 01:11
 * @desc
 */
public class AgentParallelRunner extends AbstractRunner {
    protected AdkAgentProvider tasksGeneratorAgentProvider;
    protected AdkAgentProvider taskHandlerAgentProvider;
    protected AggregatorNextPredicator aggregatorNextPredicate;
    protected AggregatorDataMerger aggregatorDataMerger;

    public static AgentParallelRunner of(String appName,
                                         AdkAgentProvider tasksGeneratorAgentProvider,
                                         AdkAgentProvider taskHandlerAgentProvider) {
        return of(appName, tasksGeneratorAgentProvider, taskHandlerAgentProvider, new DefaultAggregatorNextPredicator(), new DefaultAggregatorDataMerger());
    }

    public static AgentParallelRunner of(String appName,
                                         AdkAgentProvider tasksGeneratorAgentProvider,
                                         AdkAgentProvider taskHandlerAgentProvider,
                                         AggregatorNextPredicator aggregatorNextPredicate,
                                         AggregatorDataMerger aggregatorDataMerger) {
        return new AgentParallelRunner(appName, tasksGeneratorAgentProvider, taskHandlerAgentProvider, aggregatorNextPredicate, aggregatorDataMerger);
    }

    protected AgentParallelRunner(String appName,
                                  AdkAgentProvider tasksGeneratorAgentProvider,
                                  AdkAgentProvider taskHandlerAgentProvider,
                                  AggregatorNextPredicator aggregatorNextPredicate,
                                  AggregatorDataMerger aggregatorDataMerger) {
        super(appName);
        this.tasksGeneratorAgentProvider = tasksGeneratorAgentProvider;
        this.taskHandlerAgentProvider = taskHandlerAgentProvider;
        this.aggregatorNextPredicate = aggregatorNextPredicate;
        this.aggregatorDataMerger = aggregatorDataMerger;
    }

    @Override
    protected Graph buildGraph() {
        End end = End.of();
        Aggregator aggregator = new Aggregator(null, end, this.aggregatorNextPredicate, this.aggregatorDataMerger);

        AgentParallel tasksGeneratorAgent = new AgentParallel(tasksGeneratorAgentProvider.getName(),
                tasksGeneratorAgentProvider.getAdkAgentInvoker());
        // dynamic create edge with AgentParallel invoke/invokeStream's responseFrame
        tasksGeneratorAgent.setScatterBranchesGenerator(responseFrameFlux -> responseFrameFlux.subscribe(responseFrame -> {
            Agent taskAgent = Agent.of(taskHandlerAgentProvider.getName(), tasksGeneratorAgentProvider.getAdkAgentInvoker(), aggregator);
            PlainEdge scatterEdge = PlainEdge.of("", taskAgent);
            tasksGeneratorAgent.getEdgeList().add(scatterEdge);
        }));
        Start start = Start.of(tasksGeneratorAgent);
        return new Graph(this.appName, start);
    }

}
