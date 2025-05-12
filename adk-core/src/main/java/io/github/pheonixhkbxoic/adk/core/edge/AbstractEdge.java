package io.github.pheonixhkbxoic.adk.core.edge;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/4 18:09
 * @desc
 */
@Getter
@Setter
public abstract class AbstractEdge implements Edge {
    protected String id;
    protected String name;

    public AbstractEdge(String name) {
        this.id = AdkUtil.uuid4hex();
        this.name = name;
        if (name == null) {
            String simpleName = this.getClass().getSimpleName();
            this.name = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        }
    }

}