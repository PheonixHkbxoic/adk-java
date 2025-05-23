package io.github.pheonixhkbxoic.adk.core.spec;

import io.github.pheonixhkbxoic.adk.context.AdkContext;
import io.github.pheonixhkbxoic.adk.core.Adk;
import io.github.pheonixhkbxoic.adk.core.Status;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:00
 * @desc
 */
public interface Node extends Adk {

    String getType();

    Status getStatus();

    void updateStatus(Status status);

    AdkContext buildContextFromParent(AdkContext parent);
}
