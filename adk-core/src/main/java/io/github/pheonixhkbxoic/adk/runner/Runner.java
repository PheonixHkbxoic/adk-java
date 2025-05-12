package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.context.AdkContext;
import io.github.pheonixhkbxoic.adk.message.AdkPayload;
import io.github.pheonixhkbxoic.adk.message.ResponseFrame;
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

    List<ResponseFrame> run(AdkPayload payload);

    Flux<ResponseFrame> runAsync(AdkPayload payload);

    void setExceptionHandler(Consumer<Throwable> exceptionHandler);

    List<AdkContext> getTaskChainContextList(AdkPayload payload);

    void generatePng(OutputStream outputStream) throws IOException;

    void generateTaskPng(AdkPayload payload, OutputStream outputStream) throws IOException;
}
