package com.wildlife.observer;

/**
 * Observer Pattern: Giao diện cho các class muốn lắng nghe sự kiện
 */
public interface GameEventListener {
    void onEvent(String eventType);
}
