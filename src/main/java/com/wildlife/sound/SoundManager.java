package com.wildlife.sound;

/**
 * Singleton Pattern: Quản lý âm thanh trong game.
 */
public class SoundManager {
    private static SoundManager instance;

    private SoundManager() {
        // Tải các file âm thanh ở đây
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playSound(String soundName) {
        // Phát âm thanh tương ứng
    }
}
