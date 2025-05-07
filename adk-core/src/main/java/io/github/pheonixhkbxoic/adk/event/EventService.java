package io.github.pheonixhkbxoic.adk.event;

import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/6 06:40
 * @desc
 */
public interface EventService {


    void send(Event event);

    void addEventListener(EventListener eventListener);

    List<EventListener> getEventListeners();

}
