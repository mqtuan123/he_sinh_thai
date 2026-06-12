package com.wildlife.view;

import com.wildlife.model.animal.*;
import com.wildlife.model.base.Entity;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.observer.EventPublisher;
import com.wildlife.observer.GameEventListener;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Biểu đồ đường dân số realtime — canvas nhỏ nhúng trong ControlPanel.
 * Track 6 loài: Rabbit / Deer / Wolf / Tiger / Elephant / Duck.
 * Tự cập nhật qua Observer ENTITY_ADDED / ENTITY_REMOVED.
 * Dùng dispose() khi không còn dùng để tránh memory leak.
 */
public class PopulationChart extends Canvas implements GameEventListener {

    private static final int MAX_TICKS = 120;
    private static final int CHART_W   = 168;
    private static final int CHART_H   = 90;

    private final Deque<int[]> history = new ArrayDeque<>();
    private final WorldMap     map;

    private static final int SPECIES = 6;
    private static final Color[] COLORS = {
        Color.web("#ecf0f1"),  // Rabbit   — trắng
        Color.web("#f39c12"),  // Deer     — cam
        Color.web("#e74c3c"),  // Wolf     — đỏ
        Color.web("#9b59b6"),  // Tiger    — tím
        Color.web("#3498db"),  // Elephant — xanh
        Color.web("#f1c40f"),  // Duck     — vàng
    };
    private static final String[] LABELS = { "🐰", "🦌", "🐺", "🐯", "🐘", "🦆" };

    public PopulationChart(WorldMap map) {
        super(CHART_W, CHART_H);
        this.map = map;
        EventPublisher.getInstance().subscribe("ENTITY_ADDED",   this);
        EventPublisher.getInstance().subscribe("ENTITY_REMOVED", this);
        sample();
        redraw();
    }

    @Override
    public void onEvent(String eventType) {
        Platform.runLater(() -> { sample(); redraw(); });
    }

    public void dispose() {
        EventPublisher.getInstance().unsubscribeAll(this);
    }

    // ── Sampling ──────────────────────────────────────────────────────────────

    private void sample() {
        int[] snap = new int[SPECIES];
        for (Entity e : map.getEntities()) {
            if (!e.isAlive()) continue;
            if      (e instanceof Rabbit)   snap[0]++;
            else if (e instanceof Deer)     snap[1]++;
            else if (e instanceof Wolf)     snap[2]++;
            else if (e instanceof Tiger)    snap[3]++;
            else if (e instanceof Elephant) snap[4]++;
            else if (e instanceof Duck)     snap[5]++;
        }
        history.addLast(snap);
        if (history.size() > MAX_TICKS) history.pollFirst();
    }

    // ── Render ────────────────────────────────────────────────────────────────

    private void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        double w = CHART_W, h = CHART_H;

        // Nền
        gc.setFill(Color.color(0.07, 0.07, 0.14));
        gc.fillRect(0, 0, w, h);

        // Tiêu đề
        gc.setFill(Color.color(1, 1, 1, 0.45));
        gc.setFont(new Font("Arial", 8));
        gc.fillText("Dân số theo thời gian", 3, 9);

        // Grid
        gc.setStroke(Color.color(1, 1, 1, 0.07));
        gc.setLineWidth(0.5);
        for (int row = 1; row <= 3; row++) {
            double ly = h * 0.12 + (h * 0.78 / 4) * row;
            gc.strokeLine(0, ly, w, ly);
        }

        if (history.size() < 2) return;

        // Tìm max để scale
        int maxVal = 1;
        for (int[] snap : history)
            for (int v : snap)
                maxVal = Math.max(maxVal, v);

        int[][] arr = history.toArray(new int[0][]);
        int     n   = arr.length;

        // Vẽ đường từng loài
        for (int s = 0; s < SPECIES; s++) {
            // Bỏ loài chưa xuất hiện (tất cả = 0) để chart gọn
            boolean hasAny = false;
            for (int[] snap : arr) if (snap[s] > 0) { hasAny = true; break; }
            if (!hasAny) continue;

            gc.setStroke(COLORS[s]);
            gc.setLineWidth(1.3);
            gc.beginPath();
            for (int t = 0; t < n; t++) {
                double px = w * t / (MAX_TICKS - 1.0);
                double py = h * 0.9 - (arr[t][s] / (double) maxVal) * h * 0.78;
                if (t == 0) gc.moveTo(px, py); else gc.lineTo(px, py);
            }
            gc.stroke();

            // Điểm cuối
            int last = arr[n - 1][s];
            if (last > 0) {
                double lx = w * (n - 1) / (MAX_TICKS - 1.0);
                double ly = h * 0.9 - (last / (double) maxVal) * h * 0.78;
                gc.setFill(COLORS[s]);
                gc.fillOval(lx - 2, ly - 2, 4, 4);
            }
        }

        // Legend: icon + số hiện tại ở góc phải
        gc.setFont(new Font("Arial", 8));
        int[] lastSnap = arr[n - 1];
        int   col = 0;
        for (int s = 0; s < SPECIES; s++) {
            if (lastSnap[s] == 0) continue;
            gc.setFill(COLORS[s]);
            gc.fillText(LABELS[s] + lastSnap[s], w - 36 + (col % 2) * 18, 10 + (col / 2) * 10);
            col++;
        }
    }
}
