package com.wildlife.sound;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

/**
 * Singleton — Quản lý âm thanh trong game.
 *
 * ╔══════════════════════════════════════════════════════════╗
 * ║  Đặt file .wav vào: src/main/resources/com/wildlife/    ║
 * ║  sound/                                                   ║
 * ║    hunt.wav · eat.wav · death.wav · drink.wav            ║
 * ║    flee.wav · season.wav · ambient.wav                   ║
 * ║  File thiếu → bỏ qua, không crash.                       ║
 * ╚══════════════════════════════════════════════════════════╝
 *
 * Cải thiện:
 *  • setVolume(double) — điều chỉnh âm lượng tất cả clip.
 *  • play() throttle: không phát cùng 1 clip nếu đang chạy,
 *    tránh tiếng chồng chéo khi nhiều con vật hành động cùng lúc.
 *  • playLooping() kiểm tra đang chơi rồi không start lại.
 */
public class SoundManager {

    public enum SoundEvent {
        HUNT, EAT, DEATH, DRINK, FLEE, SEASON_CHANGE, AMBIENT
    }

    private static volatile SoundManager instance;

    private final Map<SoundEvent, AudioClip> clips   = new EnumMap<>(SoundEvent.class);
    private boolean enabled = true;
    private double  volume  = 0.6;   // 0.0 – 1.0

    private static final Map<SoundEvent, String> FILE_MAP = new EnumMap<>(SoundEvent.class);
    static {
        FILE_MAP.put(SoundEvent.HUNT,          "hunt.wav");
        FILE_MAP.put(SoundEvent.EAT,           "eat.wav");
        FILE_MAP.put(SoundEvent.DEATH,         "death.wav");
        FILE_MAP.put(SoundEvent.DRINK,         "drink.wav");
        FILE_MAP.put(SoundEvent.FLEE,          "flee.wav");
        FILE_MAP.put(SoundEvent.SEASON_CHANGE, "season.wav");
        FILE_MAP.put(SoundEvent.AMBIENT,       "ambient.wav");
    }

    private SoundManager() { loadAllClips(); }

    public static SoundManager getInstance() {
        if (instance == null) {
            synchronized (SoundManager.class) {
                if (instance == null) instance = new SoundManager();
            }
        }
        return instance;
    }

    private void loadAllClips() {
        for (Map.Entry<SoundEvent, String> entry : FILE_MAP.entrySet()) {
            try {
                URL url = getClass().getResource("/com/wildlife/sound/" + entry.getValue());
                if (url != null) {
                    AudioClip clip = new AudioClip(url.toExternalForm());
                    clip.setVolume(volume);
                    clips.put(entry.getKey(), clip);
                }
            } catch (Exception ignored) {}
        }
    }

    /** Phát 1 lần — throttle: bỏ qua nếu clip đang chạy (tránh tiếng chồng) */
    public void play(SoundEvent event) {
        if (!enabled) return;
        AudioClip clip = clips.get(event);
        if (clip != null && !clip.isPlaying()) clip.play();
    }

    /** Phát loop liên tục — dùng cho ambient. Không restart nếu đang chạy. */
    public void playLooping(SoundEvent event) {
        if (!enabled) return;
        AudioClip clip = clips.get(event);
        if (clip != null) {
            clip.setCycleCount(AudioClip.INDEFINITE);
            if (!clip.isPlaying()) clip.play();
        }
    }

    public void stop(SoundEvent event) {
        AudioClip clip = clips.get(event);
        if (clip != null) clip.stop();
    }

    public void stopAll() { clips.values().forEach(AudioClip::stop); }

    /** Thay đổi âm lượng toàn bộ clip (0.0 – 1.0) */
    public void setVolume(double vol) {
        this.volume = Math.max(0.0, Math.min(1.0, vol));
        clips.values().forEach(c -> c.setVolume(this.volume));
    }

    public double getVolume()  { return volume; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) stopAll();
    }
    public boolean isEnabled() { return enabled; }

    // Shorthand
    public void playHunt()         { play(SoundEvent.HUNT); }
    public void playEat()          { play(SoundEvent.EAT); }
    public void playDeath()        { play(SoundEvent.DEATH); }
    public void playDrink()        { play(SoundEvent.DRINK); }
    public void playFlee()         { play(SoundEvent.FLEE); }
    public void playSeasonChange() { play(SoundEvent.SEASON_CHANGE); }
}
