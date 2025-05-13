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
AdkAgentProvider qa = AdkAgentProvider.create("qaAssistant", new CustomAdkAgentInvoker());
AgentRunner runner = AgentRunner.of("Assistant", qa);
```

* chain: some agent

```java
AdkAgentProvider qa = AdkAgentProvider.create("qaAssistant", new CustomAdkAgentInvoker());
AdkAgentProvider qa2 = AdkAgentProvider.create("qaAssistant2", new CustomAdkAgentInvoker2());
AgentRunner runner = AgentRunner.of("AgentChain", qa, qa2);
```

![AgentChain](https://img.plantuml.biz/plantuml/png/SoWkIImgAStDuUK2itYv2e1aPabcVfw2bfPZUcfUYND6OcQUbfP2DI-NWeAL0bYPDOWDuXKcEXONPmB5yc1YQA96rX1jgNafG5Ojo9hy0buk1o0VQ2i0)

* router: router with some agent

```java
AdkAgentProvider qaRouter = AdkAgentProvider.create("qaRouter", new AdkAgentInvoker() {
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
AgentRouterRunner runner = AgentRouterRunner.of("AgentRouter", qaRouter, branchSelector, fallback, qa, qa2);
```

![AgentChain](https://img.plantuml.biz/plantuml/png/RP1B3i8m34JtaNA7MPOSeLrGhi0DGWWa8arAuweBnDt91uges5ZZcIVBTXz9JZGFZhEYE1jJjMI3Xn27g_Pq33FfIGWyE0DQ5AxB6eYB9MKQt6MbzjZL050oQZJGoeGovfla8QlTRVX1ald3h_QMlqZdJbBTVl6F-wQrFzTDQzjc8qNFV7GOEUjJdtxm0W00)

* custom
    - Use other existing Runner
    - Custom Runner by `extends AbstractRunner`
      like [CustomComplexRunner](https://github.com/PheonixHkbxoic/adk-java/blob/main/adk-core/src/test/java/io/github/pheonixhkbxoic/adk/test/CustomComplexRunner.java)  
      ![CustomComplexRunner](https://img.plantuml.biz/plantuml/png/vLHDJy904BqtwNyOGqAW2T7ehI2YnEZ1arVZOMZ7jc6xMtUtWX7_kzlj1r1A0cTxsiw-UJDltkxEb4QfyvIvZbsvpkYj9oby7qrT9pE1H2U2W-VDSwL5EXTfncXz7n7NdWS_hWEmam3GYoGHzs4I22QaRpQcayNKKeyFm6gLw20UjC2l8jSaROax61OuW00LGHj8GImih4Qrw8klkvycSxn7dwAEMuJ-DhYF_K365XWiTw5rp2A9XwM-4sLhL4lrH9maQj4j_soT1USpyf1Lynp_ExhZBdIJO6J2KU70uN6DfCWpSnLUXCYg6r1sTEIGSIs_Aqqj2QiuOOVd6GOfhXyp4SGmkOKhs6o0oZ-eDuRGxyD5eXuauPB2isVqYRkJFls-V27BKTCskKbiIR0D-t1zUGDDbpvecLDLqnXYrNVEfRiwftLZPcd2YMOQVDl5t-AT4uPWPZRji_S7)
    - Use low level api about `Executor`
      ```java
      public void customAndRun(){
        // define params
        Payload payload;
        Executor executor;
        Graph graph;
        RootContext rootContext = new RootContext(payload);
      
        // execute
        AdkContext ec = executor.execute(graph, rootContext);
        ec.getResponse().subscribe(responseFrame -> log.info("custom responseFrame: {}", responseFrame));
      }
      ```

### Run it

* run

```java
List<ResponseFrame> responseFrames = runner.run(payload);
```

* runAsync

```java

@Test
public void testAgentChainRunnerAsync() {
    AdkAgentProvider qa = AdkAgentProvider.create("qaAssistant", new CustomAdkAgentInvoker());
    AdkAgentProvider qa2 = AdkAgentProvider.create("qaAssistant2", new CustomAdkAgentInvoker2());
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

User payload support some key properties:

* userId
* sessionId
* stream
  When stream is true, Runners will use sse by the method `invokeStream` of AdkAgentInvoker to invoke LLM
* messages  
  Supports `AdkTextMessage`,`AdkImageMessage`,`AdkVideoMessage`,`AdkAudioMessage`,`AdkFileMessage`
* metadata

```java
 AdkPayload payload = AdkPayload.builder()
        .userId("1")
        .sessionId("2")
        .taskId(AdkUtil.uuid4hex())
        .messages(List.of(AdkTextMessage.of("hello")))
        .build();
```

### How to interact with LLM

Implements AdkAgentInvoker interface to interact with LLM by any framework, eg.LangChain4j,a2a4j

```java

@Slf4j
public class CustomAdkAgentInvoker implements AdkAgentInvoker {

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
  ![AgentRunner](https://img.plantuml.biz/plantuml/png/SoWkIImgAStDuUK2itYv2e1aPabcVfw2bfPZUcfUYND6OcQUbfP2DI-NWe9kj79HQagihXs8evnUb9gQPwLWavoVarza1PImiqco0vjZZQ6O29s5YOx5nPb0_NaKwpSYou3Kl1HqVRMWWCaluELo04e2mma0)
* AgentRouterRunner  
  ![AgentRouterRunner](https://img.plantuml.biz/plantuml/png/ZP7D3e8m3CVlItY79bnq2Iy01F4Ll1eFOrGWpWt7Y0VZkxiDyMDguWxBslxwRzUMWs7QZ4SH4V-AI6_lpdHA0gNh1gNPgD6WfXGk4G58jh76UfSKpeWRZIXJoBaIIgSsKEHLuOMo3tWuTuQtYm0-iKb_1Ki70N0s88GKybRvPcOgq7RdUpEFpEn7uhtUaPasg90-dTaRksT2L8mVNj7PvqcKzVJRFoTc-N1ULxSGrKUaj46_dni0)
* AgentLoopRunner  
  ![AgentLoopRunner](https://img.plantuml.biz/plantuml/png/bLBTQi8m5Bulz1q-AOWjo8RWJLkB2hlR6yWkOdkenPZKc6uTkdTVJ3RTMeNjBZdd-pb_yuDcIZSxxdlsvNlZLQ2eU1bdlbURGAKhAH15YvA4VfQoZY8SVG_uWGE2KX6966akkLInIMJfEhEAIGzSAjdKHf9RjNFade2nLE-9G_oI0Dus5IUCEWICTgnzgcM-GJh38qudaFlXEn5YECIWEYmLiIqL29rUp-1UJNjcHv7yaqQlZ3TCqvLy8NPQi0N7c3nCSQaoXbODVNcIA6ptD-TosrrGwqmDryt_Zoiq-Eu2Fywd8et0t2JjvNm2)
* AgentParallelRunner  
  ![AgentParallel](https://img.plantuml.biz/plantuml/png/xLEz2i8m4Du3UOU3Bbhe2xIbrab7Tt4usoCMOrAIY8FuxYOjWiOEkegPmdq_zmtVrTQXSUUJv6puSPPj4qFjBgiuw_sWSvrMaAPBGSfjqA2K9DCKhfm1F7414c68L0vbewKskGUgSbyDhiKRsLuwrnnc4TcXFTeLAJBej1bMBc0U-00DMe9Oy00Zz_2cuuGDQrIjfD6--5AFmez5I5VyO_rCh0dbou1KEnRsmz9xVbpvMpWmtNDMUz3Vsnq0)

  You can also custom yourself Runner
  like [CustomComplexRunner](https://github.com/PheonixHkbxoic/adk-java/blob/main/adk-core/src/test/java/io/github/pheonixhkbxoic/adk/test/CustomComplexRunner.java)  
  More Runners is oncoming!

### Support output image of Graph with PlantUML

* generate kinds of images with `PlantUmlGenerator` and `Graph` by `FileFormat`

```java
public void testGenerateUmlPng() {
    AgentRouterRunner runner;
    // gen uml png
    try {
        PlantUmlGenerator generator = runner.getPlantUmlGenerator();
        Graph graph = runner.getGraph();
        FileOutputStream file = new FileOutputStream("target/" + graph.getName() + ".png");
        generator.generate(graph, file, FileFormat.PNG);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```

* generate png image with Runner directly

```java
public void generatePngImage() {
    Runner runner;
    // gen uml png
    try {
        FileOutputStream file = new FileOutputStream("target/" + appName + ".png");
        runner.generatePng(file);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```

* generate svg image with Runner directly

```java
public void generateSvgImage() {
    Runner runner;
    // gen uml svg
    try {
        FileOutputStream file = new FileOutputStream("target/" + appName + ".svg");
        runner.generateSvg(file);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```

![AgentChain](https://img.plantuml.biz/plantuml/png/RP1B3i8m34JtaNA7MPOSeLrGhi0DGWWa8arAuweBnDt91uges5ZZcIVBTXz9JZGFZhEYE1jJjMI3Xn27g_Pq33FfIGWyE0DQ5AxB6eYB9MKQt6MbzjZL050oQZJGoeGovfla8QlTRVX1ald3h_QMlqZdJbBTVl6F-wQrFzTDQzjc8qNFV7GOEUjJdtxm0W00)

* generate png image with Runner Payload Task directly

```java
public void generatePngImage() {
    Runner runner;
    // gen task uml png
    try {
        FileOutputStream file = new FileOutputStream("target/" + appName + "_" + payload.getTaskId() + ".png");
        runner.generateTaskPng(payload, file);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```

![Agent Router](https://img.plantuml.biz/plantuml/png/ZP7D3e8m3CVlItY79bnq2Iy01F4Ll1eFOrGWpWt7Y0VZkxiDyMDguWxBslxwRzUMWs7QZ4SH4V-AI6_lpdHA0gNh1gNPgD6WfXGk4G58jh76UfSKpeWRZIXJoBaIIgSsKEHLuOMo3tWuTuQtYm0-iKb_1Ki70N0s88GKybRvPcOgq7RdUpEFpEn7uhtUaPasg90-dTaRksT2L8mVNj7PvqcKzVJRFoTc-N1ULxSGrKUaj46_dni0)

### Example Log

RunnerTests.testAgentRouterRunner()

```log
20:09:50.624 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: execute, start
20:09:50.629 [main] DEBUG reactor.util.Loggers -- Using Slf4j logging framework
20:09:50.630 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: execute, start
20:09:50.630 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: execute, qaRouter
20:09:50.630 [Thread-0] INFO io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: invoke, qaRouter
20:09:50.666 [Thread-0] INFO io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: invoke, qaRouter
20:09:50.666 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: route, qaRouter
20:09:50.666 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: route, qaRouter
20:09:50.666 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: execute, qaRouter
20:09:50.666 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: execute, echoAgent
20:09:50.666 [Thread-0] INFO io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: invoke, echoAgent
20:09:50.672 [Thread-0] INFO io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: invoke, echoAgent
20:09:50.672 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: execute, echoAgent
20:09:50.672 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- before event: execute, end
20:09:50.672 [Thread-0] DEBUG io.github.pheonixhkbxoic.adk.event.LogInvokeEventListener -- after event: execute, end
20:09:50.689 [main] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- before runAsync
20:09:50.703 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agent router runAsync responseFrame: ResponseFrame(message=message0)
20:09:50.808 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agent router runAsync responseFrame: ResponseFrame(message=message1)
20:09:50.920 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agent router runAsync responseFrame: ResponseFrame(message=message2)
20:09:51.031 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agent router runAsync responseFrame: ResponseFrame(message=message3)
20:09:51.143 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agent router runAsync responseFrame: ResponseFrame(message=message4)
20:09:51.255 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agent router runAsync responseFrame: ResponseFrame(message=message5)
20:09:51.366 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agent router runAsync responseFrame: ResponseFrame(message=message6)
20:09:51.474 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agent router runAsync responseFrame: ResponseFrame(message=message7)
20:09:51.583 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agent router runAsync responseFrame: ResponseFrame(message=message8)
20:09:51.691 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- agent router runAsync responseFrame: ResponseFrame(message=message9)
20:09:51.801 [boundedElastic-1] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- after runAsync
20:09:53.698 [main] INFO io.github.pheonixhkbxoic.adk.test.RunnerTests -- taskContextChain: [{"id": "606e06fb3b4f444090bd73dfc4ab77a3", "name": "root", "node": null, "metadata": {}, "activeParentId": "", "activeChildId": "9dc083f4d8754dbd9658b7dcb1417c1e"}
, {"id": "9dc083f4d8754dbd9658b7dcb1417c1e", "name": "start", "node": {"type": "start", "status": {"state": "success"}}, "metadata": {}, "activeParentId": "606e06fb3b4f444090bd73dfc4ab77a3", "activeChildId": "19eac3a325764c93b11e68d7a7455426"}
, {"id": "19eac3a325764c93b11e68d7a7455426", "name": "qaRouter", "node": {"type": "agent_router", "status": {"state": "success"}}, "metadata": {}, "activeParentId": "9dc083f4d8754dbd9658b7dcb1417c1e", "activeChildId": "35f1409202f646328005ed32b076d686"}
, {"id": "35f1409202f646328005ed32b076d686", "name": "echoAgent", "node": {"type": "agent", "status": {"state": "success"}}, "metadata": {}, "activeParentId": "19eac3a325764c93b11e68d7a7455426", "activeChildId": "09af97ff99ba4f71a872c93b0311ba3c"}
, {"id": "09af97ff99ba4f71a872c93b0311ba3c", "name": "end", "node": {"type": "end", "status": {"state": "success"}}, "metadata": {}, "activeParentId": "35f1409202f646328005ed32b076d686", "activeChildId": ""}
]
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
@startuml
start
partition **AgentRouter** {
  -[#red]->
  #green: <color:red><b>start;
  -[#red]->
  switch( <color:red><b>qaRouter? )
    case ( <color:red><b>echoAgent )
      -[#red]->
      #green: <color:red><b>echoAgent;
      -[#red]->
    case ( mathAgent )
      : mathAgent;
    case ( fallback )
      : fallback;
  endswitch
  -[#red]->
  #green: <color:red><b>end;
  -[#red]->
}
stop
@enduml
```

## Preference

[adk-core test](https://github.com/PheonixHkbxoic/adk-java/tree/main/adk-core/src/test/java/io/github/pheonixhkbxoic/adk/test)

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=PheonixHkbxoic/a2a4j-java&type=Date)](https://www.star-history.com/#PheonixHkbxoic/adk-java&Date)

