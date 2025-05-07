package io.github.pheonixhkbxoic.adk.event;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/6 06:40
 * @desc
 */
public class InMemoryEventService implements EventService {
    protected final BlockingQueue<Event> queue = new LinkedBlockingQueue<>(64);
    protected final LinkedList<EventListener> listeners = new LinkedList<>();

    public InMemoryEventService() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Event event = queue.take();
                    listeners.forEach(listener -> {
                        if (event.isComplete()) {
                            listener.after(event);
                        } else {
                            listener.before(event);
                        }
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    @Override
    public void send(Event event) {
        while (!this.queue.offer(event)) {
            // nop
        }
    }

    @Override
    public void addEventListener(EventListener eventListener) {
        this.listeners.addFirst(eventListener);
    }

    @Override
    public List<EventListener> getEventListeners() {
        return this.listeners;
    }
}
