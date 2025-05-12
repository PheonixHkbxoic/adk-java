package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.edge.PlainEdge;
import io.github.pheonixhkbxoic.adk.core.interfaces.AggregatorDataMerger;
import io.github.pheonixhkbxoic.adk.core.interfaces.AggregatorNextPredicator;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractChainNode;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.AggregatorContext;
import lombok.Getter;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:47
 * @desc
 */
@Getter
public class Aggregator extends AbstractChainNode {
    protected AggregatorNextPredicator nextPredicate;
    protected AggregatorDataMerger dataMerger;

    public Aggregator(String name, Node next,
                      AggregatorNextPredicator nextPredicate,
                      AggregatorDataMerger dataMerger) {
        this(name, next == null ? null : PlainEdge.of(next), nextPredicate, dataMerger);
    }

    public Aggregator(String name, Edge edge,
                      AggregatorNextPredicator nextPredicate,
                      AggregatorDataMerger dataMerger) {
        super(name, NodeType.AGGREGATOR, edge);
        this.nextPredicate = nextPredicate;
        this.dataMerger = dataMerger;
    }

    @Override
    public AdkContext buildContextFromParent(AdkContext parent) {
        return new AggregatorContext(parent, this);
    }
}
