package io.github.pheonixhkbxoic.adk.runtime;

import io.github.pheonixhkbxoic.adk.Payload;
import lombok.Getter;
import lombok.Setter;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/4 23:21
 * @desc
 */
@Setter
@Getter
public class BranchesInvokeContext extends AbstractBranchesExecuteContext {
    protected boolean upstream;
    protected boolean downstream;

    public BranchesInvokeContext(String id, String name, boolean async, Payload payload) {
        super(id, name, async, payload);
    }


}
