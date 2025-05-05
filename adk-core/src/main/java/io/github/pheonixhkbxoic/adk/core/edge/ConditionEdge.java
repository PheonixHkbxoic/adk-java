package io.github.pheonixhkbxoic.adk.core.edge;

import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 01:42
 * @desc
 */
public class ConditionEdge extends PlainEdge {
    protected TriPredicate<Integer, Integer, ExecuteContext> condition;

    public ConditionEdge(String name, TriPredicate<Integer, Integer, ExecuteContext> condition, Node node) {
        super(name, node, false);
        this.condition = condition;
    }

    public static ConditionEdge of(String name, TriPredicate<Integer, Integer, ExecuteContext> condition, Node next) {
        return new ConditionEdge(name, condition, next);
    }

    @Override
    public boolean match(int index, int size, ExecuteContext context) {
        return condition.test(index, size, context);
    }

}
