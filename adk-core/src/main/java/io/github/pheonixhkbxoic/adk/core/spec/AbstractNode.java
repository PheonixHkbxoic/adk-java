package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.context.AdkContext;
import io.github.pheonixhkbxoic.adk.context.ReadonlyContext;
import io.github.pheonixhkbxoic.adk.core.State;
import io.github.pheonixhkbxoic.adk.core.Status;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:17
 * @desc
 */
@Data
@Slf4j
public abstract class AbstractNode implements Node {
    protected String id;
    protected String name;
    protected String type;
    protected Status status;

    public AbstractNode() {
    }

    public AbstractNode(String type) {
        this(null, type);
    }


    public AbstractNode(String name, String type) {
        this.id = AdkUtil.uuid4hex();
        this.name = name;
        if (AdkUtil.isEmpty(name)) {
            String simpleName = this.getClass().getSimpleName();
            this.name = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        }
        this.status = Status.builder().state(State.of(State.READY)).build();
        this.type = type;
    }

    @Override
    public void updateStatus(Status status) {
        this.status = status;
    }

    @Override
    public AdkContext buildContextFromParent(AdkContext parent) {
        return new ReadonlyContext(parent, this);
    }

    @Override
    public String toString() {
        return """
                {"id": "%s", "name": "%s", "type": "%s", "status": {"state": "%s"}}""".formatted(id, name, type, status.getState().getName());
    }
}
