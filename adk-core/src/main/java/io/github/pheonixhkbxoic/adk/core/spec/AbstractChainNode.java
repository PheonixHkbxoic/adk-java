package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:37
 * @desc
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Getter
@Setter
public abstract class AbstractChainNode extends AbstractNode {
    protected Edge edge;

    public AbstractChainNode(String name, String type, Edge edge) {
        super(name, type);
        this.edge = edge;
    }


}
