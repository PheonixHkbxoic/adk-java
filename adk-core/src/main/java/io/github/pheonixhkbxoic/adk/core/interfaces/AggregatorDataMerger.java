package io.github.pheonixhkbxoic.adk.core.interfaces;

import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.AggregatorContext;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 15:13
 * @desc
 */
public interface AggregatorDataMerger extends BiConsumer<AggregatorContext, List<AdkContext>> {
}
