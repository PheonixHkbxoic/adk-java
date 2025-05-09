package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.runtime.AdkAgentInvoker;
import io.github.pheonixhkbxoic.adk.runtime.ExecutableContext;
import io.github.pheonixhkbxoic.adk.runtime.ResponseFrame;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/3 02:01
 * @desc
 */
@Slf4j
public class CustomAdkAgentInvoker2 implements AdkAgentInvoker {
    private List<ResponseFrame> preAgentData;

    @Override
    public void beforeInvoke(ExecutableContext context) {
        preAgentData = context.getResponse().toStream().toList();
        log.info("preAgentData: {}", preAgentData);
    }

    @Override
    public Mono<ResponseFrame> invoke(ExecutableContext context) {
        ResponseFrame response = new ResponseFrame();
        response.setMessage("invoker2 ok");
        return Mono.just(response);
    }

    @Override
    public Flux<ResponseFrame> invokeStream(ExecutableContext context) {
        return Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                ResponseFrame frame = new ResponseFrame();
                frame.setMessage("invoker2 message" + i);
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
