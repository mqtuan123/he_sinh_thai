package com.wildlife.engine;

import com.wildlife.model.environment.WorldMap;
import com.wildlife.view.GameView;
import javafx.animation.AnimationTimer;

/**
 * Singleton thread-safe — vòng lặp chính Update → Render.
 *
 * Cải thiện:
 *  • Delta-time cap 100ms: tránh vật thể "nhảy" khi GC pause.
 *  • FPS counter chính xác.
 *  • Sync trạng thái pause sang GameView để hiện overlay "PAUSED".
 */
public class GameLoop extends AnimationTimer {

    private static volatile GameLoop instance;

    private WorldMap map;
    private GameView view;
    private boolean  isPaused = false;

    private long lastNanoTime  = 0;
    private int  frameCount    = 0;
    private int  currentFPS    = 0;

    private long lastFrameNano = 0;
    private static final long ONE_SECOND_NS = 1_000_000_000L;
    private static final long DELTA_CAP_NS  =   100_000_000L; // 100ms

    private GameLoop() {}

    public static GameLoop getInstance() {
        if (instance == null) {
            synchronized (GameLoop.class) {
                if (instance == null) instance = new GameLoop();
            }
        }
        return instance;
    }

    public void initialize(WorldMap map, GameView view) {
        this.map  = map;
        this.view = view;
    }

    @Override
    public void handle(long now) {
        // Cập nhật pause overlay dù có pause hay không
        if (view != null) view.setPaused(isPaused);

        if (isPaused) {
            // Vẫn render (để PAUSED overlay hiện lên), nhưng không update model
            if (view != null && map != null) view.render(map);
            return;
        }

        // Delta cap
        if (lastFrameNano == 0) lastFrameNano = now;
        long delta = now - lastFrameNano;
        if (delta > DELTA_CAP_NS) delta = DELTA_CAP_NS;
        lastFrameNano = now;

        // FPS
        frameCount++;
        if (now - lastNanoTime >= ONE_SECOND_NS) {
            currentFPS   = frameCount;
            frameCount   = 0;
            lastNanoTime = now;
        }

        // Update → Render
        map.updateAll();
        view.render(map);
    }

    public void pause()        { isPaused = true; }
    public void resume()       { isPaused = false; }
    public boolean isPaused()  { return isPaused; }
    public int getCurrentFPS() { return currentFPS; }
}
