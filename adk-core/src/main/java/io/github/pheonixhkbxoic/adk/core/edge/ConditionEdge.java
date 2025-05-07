package io.github.pheonixhkbxoic.adk.core.edge;

import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 01:42
 * @desc
 */
public class ConditionEdge extends PlainEdge {
    protected TriPredicate<Integer, Integer, AdkContext> condition;

    public ConditionEdge(String name, TriPredicate<Integer, Integer, AdkContext> condition, Node node) {
        super(name, node, false);
        this.condition = condition;
    }

    public static ConditionEdge of(String name, TriPredicate<Integer, Integer, AdkContext> condition, Node next) {
        return new ConditionEdge(name, condition, next);
    }

    @Override
    public boolean match(int index, int size, AdkContext context) {
        return condition.test(index, size, context);
    }

}
