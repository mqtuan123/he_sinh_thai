package com.wildlife.engine;

import com.wildlife.app.Config;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.view.GameView;
import javafx.animation.AnimationTimer;

/**
 * Singleton Pattern: Quản lý vòng lặp chính của trò chơi (Update & Render)
 * Thread-safe (double-checked locking + volatile).
 */
public class GameLoop extends AnimationTimer {
    private static volatile GameLoop instance;
    private WorldMap map;
    private GameView view;
    
    private boolean isPaused = false;
    
    // Đo FPS
    private long lastTime = 0;
    private int frameCount = 0;
    private int currentFPS = 0;

    private GameLoop() {}

    public static GameLoop getInstance() {
        if (instance == null) {
            synchronized (GameLoop.class) {
                if (instance == null) {
                    instance = new GameLoop();
                }
            }
        }
        return instance;
    }

    public void initialize(WorldMap map, GameView view) {
        this.map = map;
        this.view = view;
    }

    @Override
    public void handle(long now) {
        if (isPaused) return;

        // Tính FPS (Cứ mỗi 1 giây cập nhật FPS)
        if (now - lastTime >= 1_000_000_000) {
            currentFPS = frameCount;
            frameCount = 0;
            lastTime = now;
        }
        frameCount++;

        // 1. Cập nhật Model
        map.updateAll();

        // 2. Vẽ lại View
        view.render(map);
    }
    
    public void pause() { isPaused = true; }
    public void resume() { isPaused = false; }
    public boolean isPaused() { return isPaused; }
    
    public int getCurrentFPS() { return currentFPS; }
}
