package io.github.pheonixhkbxoic.adk.event;

import lombok.extern.slf4j.Slf4j;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/3 02:04
 * @desc
 */
@Slf4j
public class LogEventListener implements BuildEventListener, ExecuteEventListener, InvokeEventListener {
    @Override
    public void before(Event event) {
        if (event.isInvoke()) {
            log.info("before event: {}, {}", event.getType(), event.getNodeName());
        } else {
            log.debug("before event: {}, {}", event.getType(), event.getNodeName());
        }
    }

    @Override
    public void after(Event event) {
        if (event.isInvoke()) {
            log.info("after event: {}, {}", event.getType(), event.getNodeName(), event.getError());
        } else {
            log.debug("after event: {}, {}", event.getType(), event.getNodeName(), event.getError());
        }
    }
}
