package io.github.pheonixhkbxoic.adk.runtime;

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

    public RouterContext(AdkContext parent, Node node) {
        super(parent, node);
    }


}
