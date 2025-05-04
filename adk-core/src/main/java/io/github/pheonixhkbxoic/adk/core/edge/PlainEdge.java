package io.github.pheonixhkbxoic.adk.core.edge;

import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 01:43
 * @desc
 */
public class PlainEdge extends AbstractEdge {
    protected Node node;

    public PlainEdge(String name, Node node) {
        super(name);
        this.node = node;
    }

    public static PlainEdge of(Node next) {
        return of(null, next);
    }

    public static PlainEdge of(String name, Node next) {
        return new PlainEdge(name, next);
    }

    @Override
    public boolean match(ExecuteContext context) {
        return true;
    }

    @Override
    public Node getNode() {
        return node;
    }
}
