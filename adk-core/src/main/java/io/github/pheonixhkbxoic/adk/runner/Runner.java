package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.runtime.ResponseFrame;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 15:30
 * @desc
 */
public interface Runner {

    ResponseFrame run(Payload payload);

    Flux<ResponseFrame> runAsync(Payload payload);

    void setExceptionHandler(Consumer<Throwable> exceptionHandler);
}
