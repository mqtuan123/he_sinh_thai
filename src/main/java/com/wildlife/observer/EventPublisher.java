package com.wildlife.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Observer Pattern: Singleton quản lý việc phát và lắng nghe sự kiện
 */
public class EventPublisher {
    private static EventPublisher instance;
    private Map<String, List<GameEventListener>> listeners = new HashMap<>();

    private EventPublisher() {}

    public static EventPublisher getInstance() {
        if (instance == null) {
            instance = new EventPublisher();
        }
        return instance;
    }

    public void subscribe(String eventType, GameEventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void unsubscribe(String eventType, GameEventListener listener) {
        List<GameEventListener> users = listeners.get(eventType);
        if (users != null) {
            users.remove(listener);
        }
    }

    public void publish(String eventType) {
        List<GameEventListener> users = listeners.get(eventType);
        if (users != null) {
            for (GameEventListener listener : users) {
                listener.onEvent(eventType);
            }
        }
    }
}
