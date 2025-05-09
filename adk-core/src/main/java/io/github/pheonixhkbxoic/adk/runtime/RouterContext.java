package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import lombok.Getter;
import lombok.Setter;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/4 23:21
 * @desc
 */
@Setter
@Getter
public class RouterContext extends ExecutableContext {
    private Edge selectEdge;
    
    public RouterContext(AdkContext parent, Node node) {
        super(parent, node);
    }


}
