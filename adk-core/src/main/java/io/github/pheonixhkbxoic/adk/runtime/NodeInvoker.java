package io.github.pheonixhkbxoic.adk.runtime;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 17:12
 * @desc
 */
public interface NodeInvoker {

    Mono<ResponseFrame> invoke(InvokeContext context);

    Flux<ResponseFrame> invokeStream(InvokeContext context);

    default List<ResponseFrame> readDataFromLastNode(InvokeContext context) {
        Flux<ResponseFrame> lastAgentDataFlux = context.getResponseFrameFlux();
        if (lastAgentDataFlux == null) {
            return new LinkedList<>();
        }
        return lastAgentDataFlux.toStream().toList();
    }
}
