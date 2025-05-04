package io.github.pheonixhkbxoic.adk.core.node;

import io.github.pheonixhkbxoic.adk.core.spec.AbstractGraphNode;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/4 22:49
 * @desc
 */
public class Graph extends AbstractGraphNode {
    public Graph(Start start, End end) {
        super(start, end);
    }

    public Graph(String name, Start start, End end) {
        super(name, start, end);
    }

    public Graph(String name, Start start, End end, AbstractGraphNode next) {
        super(name, start, end, next);
    }
}
