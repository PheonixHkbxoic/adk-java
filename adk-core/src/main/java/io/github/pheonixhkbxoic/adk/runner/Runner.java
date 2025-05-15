package io.github.pheonixhkbxoic.adk.runner;

import io.github.pheonixhkbxoic.adk.context.AdkContext;
import io.github.pheonixhkbxoic.adk.message.AdkPayload;
import io.github.pheonixhkbxoic.adk.message.ResponseFrame;
import net.sourceforge.plantuml.FileFormat;
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

    List<ResponseFrame> run(AdkPayload payload, Consumer<Throwable> exceptionHandler);

    Flux<ResponseFrame> runAsync(AdkPayload payload);

    Consumer<Throwable> getExceptionHandler();

    void setExceptionHandler(Consumer<Throwable> exceptionHandler);

    List<AdkContext> getTaskChainContextList(AdkPayload payload);

    void generate(OutputStream outputStream, FileFormat format) throws IOException;

    void generateTask(AdkPayload payload, OutputStream outputStream, FileFormat format) throws IOException;

    default void generatePng(OutputStream outputStream) throws IOException {
        this.generate(outputStream, FileFormat.PNG);
    }


    default void generateTaskPng(AdkPayload payload, OutputStream outputStream) throws IOException {
        this.generateTask(payload, outputStream, FileFormat.PNG);
    }

    default void generateSvg(OutputStream outputStream) throws IOException {
        this.generate(outputStream, FileFormat.SVG);
    }

    default void generateTaskSvg(AdkPayload payload, OutputStream outputStream) throws IOException {
        this.generateTask(payload, outputStream, FileFormat.SVG);
    }


}
