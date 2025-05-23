package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.context.AdkContext;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 14:56
 * @desc
 */
public class DefaultAggregatorNextPredicator implements AggregatorNextPredicator {
    @Override
    public boolean test(Integer index, Integer size, List<AdkContext> contextList) {
        return index.equals(size);
    }
}
