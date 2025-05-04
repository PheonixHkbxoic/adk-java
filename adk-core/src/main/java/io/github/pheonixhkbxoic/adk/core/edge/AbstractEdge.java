package io.github.pheonixhkbxoic.adk.core.edge;

import io.github.pheonixhkbxoic.adk.AdkUtil;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/4 18:09
 * @desc
 */
public abstract class AbstractEdge implements Edge {
    protected String id;
    protected String name;

    public AbstractEdge(String name) {
        this.id = AdkUtil.uuid4hex();
        this.name = name;
        if (AdkUtil.isEmpty(name)) {
            String simpleName = this.getClass().getSimpleName();
            this.name = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        }
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }
}
