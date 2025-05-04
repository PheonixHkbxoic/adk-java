package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.core.Adk;
import io.github.pheonixhkbxoic.adk.core.Status;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import reactor.core.publisher.Mono;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:00
 * @desc
 */
public interface Node extends Adk {

    String getType();

    Status getStatus();

    Mono<ExecuteContext> build(ExecuteContext parentContext);

    Mono<ExecuteContext> execute(ExecuteContext context);
}
