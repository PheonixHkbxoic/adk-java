package io.github.pheonixhkbxoic.adk.core.edge;

import io.github.pheonixhkbxoic.adk.runtime.ExecutableContext;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/9 16:21
 * @desc route by field name in metadata
 */
public class DefaultRouterSelector extends DefaultBranchSelector {
    private final String metadataFieldName;

    public DefaultRouterSelector(String metadataFieldName) {
        this.metadataFieldName = metadataFieldName;
    }

    @Override
    public boolean select(Edge edge, int index, int size, ExecutableContext context) {
        if (context.getMetadata() == null) {
            return false;
        }
        String metadataFieldValue = context.getMetadata().getOrDefault(metadataFieldName, "").toString();
        return metadataFieldValue.equalsIgnoreCase(edge.getName());
    }
}
