package io.github.pheonixhkbxoic.adk;

import io.github.pheonixhkbxoic.adk.event.*;
import io.github.pheonixhkbxoic.adk.event.EventListener;

import java.util.*;


/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 18:59
 * @desc
 */
public class AdkUtil {

    public static boolean isEmpty(CharSequence c) {
        return c == null || c.isEmpty();
    }

    public static <T> boolean isEmpty(Collection<T> c) {
        return c == null || c.isEmpty();
    }

    public static String uuid4hex() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase(Locale.ROOT);
    }


    public static void notifyBuildEvent(List<EventListener> listeners, Event event, boolean after) {
        LinkedList<EventListener> list = new LinkedList<>(listeners);
        boolean logEventListenerExist = listeners.stream().anyMatch(t -> t instanceof LogEventListener);
        if (!logEventListenerExist) {
            list.addFirst(new LogEventListener());
        }

        if (after) {
            list.stream()
                    .filter(t -> t instanceof BuildEventListener)
                    .forEach(t -> t.after(event));
        } else {
            list.stream()
                    .filter(t -> t instanceof BuildEventListener)
                    .forEach(t -> t.before(event));
        }
    }

    public static void notifyExecuteEvent(List<EventListener> listeners, Event event, boolean after) {
        LinkedList<EventListener> list = new LinkedList<>(listeners);
        boolean logEventListenerExist = listeners.stream().anyMatch(t -> t instanceof LogEventListener);
        if (!logEventListenerExist) {
            list.addFirst(new LogEventListener());
        }

        if (after) {
            list.stream()
                    .filter(t -> t instanceof ExecuteEventListener)
                    .forEach(t -> t.after(event));
        } else {
            list.stream()
                    .filter(t -> t instanceof ExecuteEventListener)
                    .forEach(t -> t.before(event));
        }
    }

    public static void notifyInvokeEvent(List<EventListener> listeners, Event event, boolean after) {
        LinkedList<EventListener> list = new LinkedList<>(listeners);
        boolean logEventListenerExist = listeners.stream().anyMatch(t -> t instanceof LogEventListener);
        if (!logEventListenerExist) {
            list.addFirst(new LogEventListener());
        }

        if (after) {
            list.stream()
                    .filter(t -> t instanceof InvokeEventListener)
                    .forEach(t -> t.after(event));
        } else {
            list.stream()
                    .filter(t -> t instanceof InvokeEventListener)
                    .forEach(t -> t.before(event));
        }
    }


}
