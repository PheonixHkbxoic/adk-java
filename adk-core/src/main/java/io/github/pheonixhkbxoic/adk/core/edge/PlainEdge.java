package io.github.pheonixhkbxoic.adk.core.edge;

import io.github.pheonixhkbxoic.adk.core.spec.Node;
import io.github.pheonixhkbxoic.adk.runtime.ExecuteContext;
import lombok.Getter;
import lombok.Setter;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 01:43
 * @desc
 */
@Getter
@Setter
public class PlainEdge extends AbstractEdge {
    protected Node node;
    protected boolean fallback;

    public PlainEdge(String name, Node node, boolean fallback) {
        super(name);
        this.node = node;
        this.fallback = fallback;
    }

    public static PlainEdge of(Node next) {
        return of(null, next, false);
    }

    public static PlainEdge of(String name, Node next) {
        return of(name, next, false);
    }

    public static PlainEdge of(String name, Node next, boolean fallback) {
        return new PlainEdge(name, next, fallback);
    }

    @Override
    public boolean match(int index, int size, ExecuteContext context) {
        return true;
    }

    @Override
    public Node getNode() {
        return node;
    }
}
