package io.github.pheonixhkbxoic.adk.core.edge;

import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;

import java.util.function.Predicate;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 01:42
 * @desc
 */
public class ConditionEdge extends PlainEdge {
    protected Predicate<ExecuteContext> condition;

    public ConditionEdge(String name, Predicate<ExecuteContext> condition, Node node) {
        super(name, node);
        this.condition = condition;
    }

    public static ConditionEdge of(String name, Predicate<ExecuteContext> condition, Node next) {
        return new ConditionEdge(name, condition, next);
    }

    @Override
    public boolean match(ExecuteContext context) {
        return condition.test(context);
    }

}
