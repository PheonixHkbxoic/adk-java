# adk-java

An open-source, code-first Java toolkit for building, evaluating, and running sophisticated AI agents with flexibility
and control.  
adk-java is a Java implementation of Agent Develop Kit for orchestrating Multi-Agents

---

## ✨ Key Features

- **Rich Framework**: Utilize any AI frameworks, eg. LangChain4j,SpringAI.

- **Code-First Development**: Define agent logic, and orchestration
  directly in Java for ultimate flexibility, testability, and versioning.

- **Modular Multi-Agent Systems**: Design scalable applications by composing
  multiple specialized agents into flexible hierarchies.

- **Strong Ecosystem**: seamlessly integrate with maven, Spring And Easily deploy application on JVM.

## Usage

### Orchestration Agent node into graph

* simple: just one agent

```java
AgentProvider qa = AgentProvider.create("qaAssistant", new CustomAgentInvoker());
AgentRunner runner = AgentRunner.of("Assistant", qa);
```

* chain: some agent

```java
AgentProvider qa = AgentProvider.create("qaAssistant", new CustomAgentInvoker());
AgentProvider qa2 = AgentProvider.create("qaAssistant2", new CustomAgentInvoker2());
AgentRunner runner = AgentRunner.of("AgentChain", qa, qa2);
```

![AgentChain](https://img.plantuml.biz/plantuml/png/SoWkIImgAStDuUK2itYv2e1aPabcVfw2bfPZUcfUYND6OcQUbfP2DI-NWeAL0bYPDOWDuXKcEXONPmB5yc1YQA96rX1jgNafG5Ojo9hy0buk1o0VQ2i0)

* router: router with some agent

```java
AgentProvider qaRouter = AgentProvider.create("qaRouter", new AgentInvoker() {
    @Override
    public Mono<ResponseFrame> invoke(ExecutableContext context) {
        // mock request llm and response
        Map<String, Object> metadata = Map.of("activeAgent", "echoAgent", "answer", "router self answer..balabala...");
        context.setMetadata(metadata);
        return Mono.empty();
    }

    @Override
    public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
        return this.invoke(context).flux();
    }
});

BranchSelector branchSelector = (edge, index, size, context) -> {
    Object activeAgent = context.getMetadata().get("activeAgent");
    return activeAgent != null && activeAgent.toString().equalsIgnoreCase(edge.getName());
};

AgentProvider qa = AgentProvider.create("echoAgent", new CustomAgentInvoker());
AgentProvider qa2 = AgentProvider.create("mathAgent", new CustomAgentInvoker2());
AgentProvider fallback = AgentProvider.create("fallback", new AgentInvoker() {
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
AgentRouterRunner runner = AgentRouterRunner.of("AgentRouter", qaRouter, branchSelector, fallback, qa, qa2);
```

![AgentChain](https://img.plantuml.biz/plantuml/png/RP1B3i8m34JtaNA7MPOSeLrGhi0DGWWa8arAuweBnDt91uges5ZZcIVBTXz9JZGFZhEYE1jJjMI3Xn27g_Pq33FfIGWyE0DQ5AxB6eYB9MKQt6MbzjZL050oQZJGoeGovfla8QlTRVX1ald3h_QMlqZdJbBTVl6F-wQrFzTDQzjc8qNFV7GOEUjJdtxm0W00)

* custom
    - Use other existing Runner
    - Custom Runner by `extends AbstractRunner` like `AgentRunner` or `AgentRouterRunner`
    - Use low level api about `Executor`
      ```java
      public void customAndRun(){
        // define params
        Payload payload = null;
        Executor executor = null;
        Graph graph = null;
        RootContext rootContext = new RootContext(payload);
      
        // execute
        AdkContext ec = executor.execute(graph, rootContext);
        ec.getResponse().subscribe(responseFrame -> log.info("custom responseFrame: {}", responseFrame));
      }
      ```

### Run it

* run

```java
ResponseFrame responseFrame = runner.run(payload);
```

* runAsync

```java

@Test
public void testAgentChainRunnerAsync() {
    AgentProvider qa = AgentProvider.create("qaAssistant", new CustomAgentInvoker());
    AgentProvider qa2 = AgentProvider.create("qaAssistant2", new CustomAgentInvoker2());
    AgentRunner runner = AgentRunner.of("AgentChain", qa, qa2);

    Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").stream(true).build();

    // runAsync
    runner.runAsync(payload)
            .subscribe(responseFrame -> log.info("agent runAsync responseFrame: {}", responseFrame));

    try {
        TimeUnit.SECONDS.sleep(3);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}
```

### Payload

user payload support some key properties:

* userId
* sessionId
* stream
  When stream is true, Runners will use sse by the method `invokeStream` of AgentInvoker to invoke LLM

```java
Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").stream(true).build();
```

### How to interact with LLM

Implements AgentInvoker interface to interact with LLM by any framework, eg.LangChain4j,a2a4j

```java

@Slf4j
public class CustomAgentInvoker implements AgentInvoker {

    @Override
    public Mono<ResponseFrame> invoke(ExecutableContext context) {
        ResponseFrame response = new ResponseFrame();
        response.setMessage("ok");
        return Mono.just(response);
    }

    @Override
    public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
        return Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                ResponseFrame frame = new ResponseFrame();
                frame.setMessage("message" + i);
                sink.next(frame);
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            sink.complete();
        });
    }
}
```

### Executor

Runner use `new Executor(new InMemorySessionService(), new InMemoryEventService())` as default executor.  
Your also can use custom executor by `initExecutor` of Runner.

```java
AgentRunner runner = AgentRunner.of("Assistant", qa).initExecutor(executor);
```

### Runner

Runner is high level api for Orchestrating your agents and run it synchronously or asynchronously.

* AgentRunner
* AgentRouterRunner

More Runner is oncoming!

### Support output PNG of Graph with PlantUML

```java
public void testGenerateUmlPng() {
    AgentRouterRunner runner = null;
    // gen uml png
    try {
        PlantUmlGenerator generator = runner.getPlantUmlGenerator();
        Graph graph = runner.getGraph();
        FileOutputStream file = new FileOutputStream("target/" + graph.getName() + ".png");
        generator.generatePng(graph, file);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```

### Example Log

RunnerTests.testAgenticRouterRunner()

```log
17:14:39.228 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: execute, start
17:14:39.233 [main] DEBUG reactor.util.Loggers -- Using Slf4j logging framework
17:14:39.234 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: execute, start
17:14:39.234 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: execute, qaRouter
17:14:39.234 [Thread-0] INFO io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: invoke, qaRouter
17:14:39.267 [Thread-0] INFO io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: invoke, qaRouter
17:14:39.268 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: route, qaRouter
17:14:39.268 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: route, qaRouter
17:14:39.268 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: execute, qaRouter
17:14:39.268 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: execute, echoAgent
17:14:39.268 [Thread-0] INFO io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: invoke, echoAgent
17:14:39.271 [Thread-0] INFO io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: invoke, echoAgent
17:14:39.271 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: execute, echoAgent
17:14:39.271 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: execute, end
17:14:39.271 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: execute, end
17:14:39.287 [main] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- before runAsync
17:14:39.301 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message0)
17:14:39.405 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message1)
17:14:39.511 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message2)
17:14:39.621 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message3)
17:14:39.728 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message4)
17:14:39.836 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message5)
17:14:39.945 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message6)
17:14:40.053 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message7)
17:14:40.160 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message8)
17:14:40.270 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message9)
17:14:40.379 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- after runAsync
@startuml
start
partition **AgentRouter** {
  : start;
  switch( qaRouter? )
    case ( echoAgent )
      : echoAgent;
    case ( mathAgent )
      : mathAgent;
    case ( fallback )
      : fallback;
  endswitch
  : end;
}
stop
@enduml
```

## Preference

[adk-core test](https://github.com/PheonixHkbxoic/adk-java/tree/main/adk-core/src/test/java/io/github/pheonixhkbxoic/adk/test)

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=PheonixHkbxoic/a2a4j-java&type=Date)](https://www.star-history.com/#PheonixHkbxoic/adk-java&Date)

