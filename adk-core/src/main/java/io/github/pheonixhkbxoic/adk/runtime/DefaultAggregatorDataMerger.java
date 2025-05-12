package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.context.AdkContext;
import io.github.pheonixhkbxoic.adk.context.AggregatorContext;
import io.github.pheonixhkbxoic.adk.message.ResponseFrame;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 15:11
 * @desc
 */
public class DefaultAggregatorDataMerger implements AggregatorDataMerger {
    @Override
    public void accept(AggregatorContext aggregatorContext, List<AdkContext> contextList) {
        Flux<ResponseFrame> sumData = Flux.concat(contextList.stream().map(AdkContext::getResponse).toList());
        aggregatorContext.setResponse(sumData);
    }
}
