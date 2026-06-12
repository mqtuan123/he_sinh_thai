package com.wildlife.sound;

import javafx.scene.media.AudioClip;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Pattern: Quản lý âm thanh trong game.
 * Thread-safe (double-checked locking).
 * Tự fallback null nếu thiếu file WAV -> không crash game.
 */
public class SoundManager {
    private static volatile SoundManager instance;

    private Map<String, AudioClip> clips = new HashMap<>();

    private static final String[] SOUND_NAMES = {
        "hunt", "eat", "death", "drink", "flee", "season", "ambient"
    };

    private SoundManager() {
        for (String name : SOUND_NAMES) {
            loadClip(name);
        }
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            synchronized (SoundManager.class) {
                if (instance == null) {
                    instance = new SoundManager();
                }
            }
        }
        return instance;
    }

    private void loadClip(String soundName) {
        try {
            String path = "/com/wildlife/sound/" + soundName + ".wav";
            java.net.URL resource = SoundManager.class.getResource(path);
            if (resource != null) {
                clips.put(soundName, new AudioClip(resource.toExternalForm()));
            } else {
                clips.put(soundName, null);
            }
        } catch (Exception e) {
            // Bất kỳ lỗi tải file -> coi như không có âm thanh, không crash
            clips.put(soundName, null);
        }
    }

    /**
     * Phát âm thanh tương ứng. Nếu file không tồn tại, bỏ qua an toàn (không crash).
     */
    public void playSound(String soundName) {
        AudioClip clip = clips.get(soundName);
        if (clip != null) {
            clip.play();
        }
        // null -> im lặng, không làm gì cả
    }

    /**
     * Phát lặp (loop) - dùng cho ambient sound.
     */
    public void loopSound(String soundName) {
        AudioClip clip = clips.get(soundName);
        if (clip != null) {
            clip.setCycleCount(AudioClip.INDEFINITE);
            clip.play();
        }
    }

    public void stopSound(String soundName) {
        AudioClip clip = clips.get(soundName);
        if (clip != null) {
            clip.stop();
        }
    }
}
