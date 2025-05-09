package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.Payload;
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.ResponseFrame;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
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

    List<AdkContext> getTaskChainContextList(Payload payload);

    void generatePng(OutputStream outputStream) throws IOException;

    void generateTaskPng(Payload payload, OutputStream outputStream) throws IOException;
}
