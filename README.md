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
Agent qa = Agent.create("qaAssistant", new CustomAgentInvoker());
AgentRunner runner = AgentRunner.of("assistant", qa);
```

* chain: some agent

```java
Agent qa = Agent.create("qaAssistant", new CustomAgentInvoker());
Agent qa2 = Agent.create("qaAssistant2", new CustomAgentInvoker2());
AgentRunner runner = AgentRunner.of("assistant", qa, qa2);
```

* router: router with some agent

```java
Agent qaRouter = Agent.create("qaRouter", new AgentInvoker() {
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

Agent qa = Agent.create("echoAgent", new CustomAgentInvoker());
Agent qa2 = Agent.create("mathAgent", new CustomAgentInvoker2());
Agent fallback = Agent.create("fallback", new AgentInvoker() {
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
```

### Run it

* run

```java
ResponseFrame responseFrame = runner.run(payload);
```

* runAsync

```java

@Test
public void testAgentRunner2Async() {
    Agent qa = Agent.create("qaAssistant", new CustomAgentInvoker());
    Agent qa2 = Agent.create("qaAssistant2", new CustomAgentInvoker2());
    AgentRunner runner = AgentRunner.of("assistant", qa, qa2);

    Payload payload = Payload.builder().userId("1").sessionId("2").message("hello").stream(true).build();

    // runAsync
    runner.runAsync(payload)
            .subscribe(responseFrame -> log.info("agent runAsync responseFrame: {}", responseFrame));

    try {
        // wait runner terminal
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
22:10:21.382 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: execute, start
22:10:21.382 [main] DEBUG reactor.util.Loggers -- Using Slf4j logging framework
22:10:21.390 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: execute, start
22:10:21.390 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: execute, qaRouter
22:10:21.390 [Thread-0] INFO io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: agent_invoke, qaRouter
22:10:21.421 [Thread-0] INFO io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: agent_invoke, qaRouter
22:10:21.421 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: route, qaRouter
22:10:21.421 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: route, qaRouter
22:10:21.421 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: execute, qaRouter
22:10:21.421 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: execute, echoAgent
22:10:21.421 [Thread-0] INFO io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: agent_invoke, echoAgent
22:10:21.421 [Thread-0] INFO io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: agent_invoke, echoAgent
22:10:21.421 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: execute, echoAgent
22:10:21.421 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: execute, end
22:10:21.421 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: execute, end
22:10:21.453 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message0)
22:10:21.563 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message1)
22:10:21.673 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message2)
22:10:21.783 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message3)
22:10:21.893 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message4)
22:10:22.003 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message5)
22:10:22.113 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message6)
22:10:22.222 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message7)
22:10:22.339 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message8)
22:10:22.448 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agentic router runAsync responseFrame: ResponseFrame(message=message9)
@startuml
start
partition **assistant** {
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

