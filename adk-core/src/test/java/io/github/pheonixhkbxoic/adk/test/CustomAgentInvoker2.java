package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.runtime.AgentInvoker;
import io.github.pheonixhkbxoic.adk.runtime.InvokeContext;
import io.github.pheonixhkbxoic.adk.runtime.ResponseFrame;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/3 02:01
 * @desc
 */
public class CustomAgentInvoker2 implements AgentInvoker {

    @Override
    public Mono<ResponseFrame> invoke(InvokeContext context) {
        ResponseFrame response = new ResponseFrame();
        response.setMessage("invoker2 ok");
        return Mono.just(response);
    }

    @Override
    public Flux<ResponseFrame> invokeStream(InvokeContext context) {
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
