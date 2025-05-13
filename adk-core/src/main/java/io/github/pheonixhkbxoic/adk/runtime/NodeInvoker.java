package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.context.ExecutableContext;
import io.github.pheonixhkbxoic.adk.message.ResponseFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 17:12
 * @desc
 */
public interface NodeInvoker {

    /**
     * handle pre agent response before invoke or invokeStream
     *
     * @param context current context
     */
    default void beforeInvoke(ExecutableContext context) {
        Logger log = LoggerFactory.getLogger(this.getClass());
        Flux<ResponseFrame> lastAgentDataFlux = context.getResponse();
        lastAgentDataFlux.subscribe(responseFrame -> log.info("before invoke response: {}, context:{}", responseFrame, context.getName()));
    }

    Mono<ResponseFrame> invoke(ExecutableContext context);

    Flux<ResponseFrame> invokeStream(ExecutableContext context);
}
