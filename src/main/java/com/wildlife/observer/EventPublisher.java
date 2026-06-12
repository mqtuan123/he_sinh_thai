package com.wildlife.observer;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Observer Pattern — Singleton thread-safe.
 * Fix: listeners dùng CopyOnWriteArrayList tránh ConcurrentModificationException.
 * Fix: unsubscribe() hoạt động đúng → không còn memory leak khi đổi bản đồ.
 */
public class EventPublisher {

    private static volatile EventPublisher instance;

    // CopyOnWriteArrayList: an toàn khi publish() gọi từ AnimationTimer
    private final Map<String, List<GameEventListener>> listeners = new HashMap<>();

    private EventPublisher() {}

    public static EventPublisher getInstance() {
        if (instance == null) {
            synchronized (EventPublisher.class) {
                if (instance == null) instance = new EventPublisher();
            }
        }
        return instance;
    }

    public synchronized void subscribe(String eventType, GameEventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public synchronized void unsubscribe(String eventType, GameEventListener listener) {
        List<GameEventListener> list = listeners.get(eventType);
        if (list != null) list.remove(listener);
    }

    /** Unsubscribe listener khỏi TẤT CẢ event types */
    public synchronized void unsubscribeAll(GameEventListener listener) {
        listeners.values().forEach(list -> list.remove(listener));
    }

    public void publish(String eventType) {
        List<GameEventListener> list = listeners.get(eventType);
        if (list != null) list.forEach(l -> l.onEvent(eventType));
    }
}
