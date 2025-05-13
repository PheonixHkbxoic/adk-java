package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.context.ExecutableContext;
import io.github.pheonixhkbxoic.adk.context.LoopContext;
import io.github.pheonixhkbxoic.adk.core.AdkAgentProvider;
import io.github.pheonixhkbxoic.adk.core.edge.DefaultRouterSelector;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.node.*;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.message.ResponseFrame;
import io.github.pheonixhkbxoic.adk.runner.AbstractRunner;
import io.github.pheonixhkbxoic.adk.runtime.AdkAgentInvoker;
import io.github.pheonixhkbxoic.adk.runtime.BranchSelector;
import io.github.pheonixhkbxoic.adk.runtime.DefaultAggregatorDataMerger;
import io.github.pheonixhkbxoic.adk.runtime.DefaultAggregatorNextPredicator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/13 14:38
 * @desc
 */
@Slf4j
public class CustomComplexRunner extends AbstractRunner {

    protected CustomComplexRunner(String appName) {
        super(appName);
    }

    @Override
    protected Graph buildGraph() {
        End end = End.of();

        // loop agent
        Loop loopAgent = buildLoopAgent(end);

        // parallel agent
        AgentParallel parallelAgent = buildParallelAgent(loopAgent);

        // router agent
        AgentRouter agentRouter = buildRouterAgent(parallelAgent);

        Start start = Start.of(agentRouter);
        return new Graph(this.appName, start);
    }

    private AgentRouter buildRouterAgent(AgentParallel parallelAgent) {
        String routeFieldName = "activeAgent";
        AdkAgentProvider qaRouter = AdkAgentProvider.create("RouterAgent", new AdkAgentInvoker() {
            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                // mock request llm and response
                Map<String, Object> metadata = Map.of(routeFieldName, "echoAgent", "answer", "router self answer..balabala...");
                context.setMetadata(new HashMap<>(metadata));
                return Mono.empty();
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                return this.invoke(context).flux();
            }
        });
        BranchSelector branchSelector = new DefaultRouterSelector(routeFieldName);
        AdkAgentProvider qa = AdkAgentProvider.create("echoAgent", new CustomAdkAgentInvoker());
        AdkAgentProvider qa2 = AdkAgentProvider.create("mathAgent", new CustomAdkAgentInvoker2());
        AdkAgentProvider fallback = AdkAgentProvider.create("fallback", new AdkAgentInvoker() {
            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                String answer = (String) context.getMetadata().get("answer");
                ResponseFrame response = new ResponseFrame();
                response.setMessage(answer);
                return Mono.just(response);
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                return this.invoke(context).flux();
            }
        });
        List<Edge> edgeList = Stream.of(qa, qa2)
                .map(adkAgentProvider -> {
                    Agent agent = Agent.of(adkAgentProvider.getName(), adkAgentProvider.getAdkAgentInvoker(), parallelAgent);
                    PlainEdge edge = PlainEdge.of(adkAgentProvider.getName(), agent);
                    return (Edge) edge;
                })
                .toList();
        Agent agentFallback = Agent.of(fallback.getName(), fallback.getAdkAgentInvoker(), parallelAgent);
        ArrayList<Edge> edges = new ArrayList<>(edgeList);
        edges.add(PlainEdge.of(agentFallback.getName(), agentFallback, true));
        return new AgentRouter(qaRouter.getName(), qaRouter.getAdkAgentInvoker(), edges, branchSelector);
    }

    private Loop buildLoopAgent(End end) {
        AdkAgentProvider first = AdkAgentProvider.create("qaAgent", new AdkAgentInvoker() {
            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                LoopContext loopContext = context.getLoopContext();
                if (loopContext != null) {
                    log.info("node in loop: {}, loop name: {}, loop epoch: {}, loop maxEpoch: {}",
                            context.getNode().getName(), loopContext.getName(), loopContext.getEpoch(), loopContext.getMaxEpoch());
                    String message = String.format("round %d %s: %s", loopContext.getEpoch() + 1, context.getName(), "message xxx");
                    return Mono.just(ResponseFrame.of(message));
                }
                return Mono.empty();
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                return this.invoke(context).flux();
            }
        });
        AdkAgentProvider second = AdkAgentProvider.create("evaluateAgent", new AdkAgentInvoker() {
            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                if (context.getMetadata() == null) {
                    context.setMetadata(new HashMap<>());
                }

                // evaluate qa score, exit if score > 80
                if (context.getLoopContext() != null) {
                    // mock evaluate score
                    double score = Double.parseDouble(context.getMetadata().getOrDefault("score", "0").toString());
                    score += 42;
                    context.getMetadata().put("score", score);
                    if (score > 80) {
                        context.getLoopContext().setBreaked(true);
                    }
                }
                return Mono.justOrEmpty(context.getResponse()
                        .map(r -> {
                            r.setMessage(r.getMessage() + " evaluated");
                            return r;
                        })
                        .blockFirst());
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                return this.invoke(context).flux();
            }
        });
        List<AdkAgentProvider> adkAgentProviderList = new ArrayList<>(List.of(first, second));
        Node next = null;
        Collections.reverse(adkAgentProviderList);
        for (AdkAgentProvider adkAgentProvider : adkAgentProviderList) {
            next = Agent.of(adkAgentProvider.getName(), adkAgentProvider.getAdkAgentInvoker(), next);
        }
        Node entry = next;
        return new Loop("LoopAgent", entry, 3, end);
    }

    private static AgentParallel buildParallelAgent(Loop loopAgent) {
        AdkAgentProvider tasksGeneratorAgentProvider = AdkAgentProvider.create("tasksGeneratorAgent", new AdkAgentInvoker() {
            @Override
            public void beforeInvoke(ExecutableContext context) {

            }

            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                return Mono.empty();
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                // mock agent generate tasks
                return Flux.create(sink -> {
                    for (int i = 0; i < 4; i++) {
                        sink.next(ResponseFrame.of(String.format("task-%02d", i)));
                    }
                    sink.complete();
                });
            }
        });

        AdkAgentProvider taskHandlerAgentProvider = AdkAgentProvider.create("taskHandlerAgent", new AdkAgentInvoker() {
            @Override
            public Mono<ResponseFrame> invoke(ExecutableContext context) {
                return Mono.just(ResponseFrame.of("task handler message xxx"));
            }

            @Override
            public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
                return this.invoke(context).flux();
            }
        });

        Aggregator aggregator = new Aggregator(null, loopAgent, new DefaultAggregatorNextPredicator(), new DefaultAggregatorDataMerger());

        AgentParallel parallelAgent = new AgentParallel(tasksGeneratorAgentProvider.getName(), tasksGeneratorAgentProvider.getAdkAgentInvoker());
        // dynamic create edge with AgentParallel invoke/invokeStream's responseFrame
        parallelAgent.setScatterBranchesGenerator(responseFrameFlux -> responseFrameFlux.subscribe(responseFrame -> {
            Agent taskAgent = Agent.of(taskHandlerAgentProvider.getName(), tasksGeneratorAgentProvider.getAdkAgentInvoker(), aggregator);
            PlainEdge scatterEdge = PlainEdge.of("", taskAgent);
            parallelAgent.getEdgeList().add(scatterEdge);
        }));
        return parallelAgent;
    }
}
