package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.core.NodeType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:51
 * @desc
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Getter
@Setter
public abstract class AbstractGroupNode extends AbstractNode {
    protected Node entry;


    public AbstractGroupNode(String name, Node entry) {
        super(name, NodeType.GROUP);
        this.entry = entry;
    }


}
