package com.wildlife.view;

import com.wildlife.app.Config;
import com.wildlife.model.animal.Animal;
import com.wildlife.model.base.Entity;
import com.wildlife.model.enums.Season;
import com.wildlife.model.enums.SpawnMode;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.environment.Zone;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.function.BiConsumer;

/**
 * GameView — Canvas chính.
 *
 * Cải thiện:
 *  • Click vào con vật → popup thông tin (HP, Hunger, Thirst, State).
 *  • drawSeasonOverlay() — hạt tuyết mùa Đông, phấn hoa mùa Xuân,
 *    lá vàng mùa Thu (particle giả bằng random seed cố định theo frame).
 *  • Nền gradient theo mùa.
 *  • Spawn mode crosshair cursor.
 *  • Overlay PAUSED khi game bị dừng.
 */
public class GameView extends Canvas {

    private final GraphicsContext gc;

    private BiConsumer<Double, Double> onMapClick;
    private SpawnMode currentSpawnMode = SpawnMode.NONE;

    // Popup: con vật đang được select
    private Animal selectedAnimal = null;
    private WorldMap lastMap;

    // Particle seed thay đổi mỗi frame → dùng để tạo hiệu ứng chuyển động giả
    private long frameTick = 0;

    // Pause state (set từ ngoài)
    private boolean paused = false;

    public GameView() {
        super(Config.MAP_WIDTH, Config.MAP_HEIGHT);
        this.gc = this.getGraphicsContext2D();

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            double mx = event.getX(), my = event.getY();

            // Ưu tiên click vào con vật
            if (lastMap != null) {
                Animal clicked = findAnimalAt(mx, my);
                if (clicked != null) {
                    selectedAnimal = (selectedAnimal == clicked) ? null : clicked;
                    return;
                }
            }

            // Click spawm
            selectedAnimal = null;
            if (onMapClick != null && currentSpawnMode != SpawnMode.NONE) {
                onMapClick.accept(mx, my);
            }
        });

        this.addEventHandler(MouseEvent.MOUSE_MOVED, event ->
            setCursor(currentSpawnMode != SpawnMode.NONE
                ? javafx.scene.Cursor.CROSSHAIR
                : javafx.scene.Cursor.DEFAULT)
        );
    }

    // ── Render ────────────────────────────────────────────────────────────────

    public void render(WorldMap map) {
        this.lastMap = map;
        frameTick++;

        // Nền theo mùa
        drawBackground(map.getCurrentSeason());

        // Địa hình
        for (Zone zone : map.getZones()) zone.render(gc);

        // Entities
        for (Entity e : map.getEntities()) e.render(gc);

        // Particle overlay theo mùa
        drawSeasonOverlay(map.getCurrentSeason());

        // Selection highlight + popup
        if (selectedAnimal != null && selectedAnimal.isAlive()) {
            drawSelectionRing();
            drawInfoPopup();
        } else {
            selectedAnimal = null;
        }

        // Spawn mode overlay
        if (currentSpawnMode != SpawnMode.NONE) drawSpawnOverlay();

        // Pause overlay
        if (paused) drawPauseOverlay();
    }

    // ── Background ────────────────────────────────────────────────────────────

    private void drawBackground(Season s) {
        switch (s) {
            case SPRING: gc.setFill(Color.web("#c8e6c9")); break;
            case SUMMER: gc.setFill(Color.web("#dcedc8")); break;
            case AUTUMN: gc.setFill(Color.web("#ffe0b2")); break;
            case WINTER: gc.setFill(Color.web("#e3f2fd")); break;
        }
        gc.fillRect(0, 0, getWidth(), getHeight());
    }

    // ── Season particle overlay ───────────────────────────────────────────────

    private void drawSeasonOverlay(Season season) {
        switch (season) {
            case WINTER: drawSnow();   break;
            case SPRING: drawPollen(); break;
            case AUTUMN: drawLeaves(); break;
            case SUMMER: /* nắng — không cần particle */  break;
        }
    }

    /** Tuyết rơi — 40 hạt, vị trí xoay vòng theo frameTick */
    private void drawSnow() {
        gc.setFill(Color.color(1, 1, 1, 0.7));
        int n = 40;
        double w = getWidth(), h = getHeight();
        for (int i = 0; i < n; i++) {
            double seed = (i * 137.5 + frameTick * 0.7) % w;
            double px   = seed % w;
            double py   = (i * 43 + frameTick * 1.1) % h;
            double size = 1.5 + (i % 3) * 1.0;
            gc.fillOval(px, py, size, size);
        }
    }

    /** Phấn hoa mùa Xuân — 25 hạt vàng nhạt */
    private void drawPollen() {
        int n = 25;
        double w = getWidth(), h = getHeight();
        for (int i = 0; i < n; i++) {
            double seed = (i * 97.3 + frameTick * 0.4) % w;
            double px   = seed % w;
            double py   = (i * 61 + frameTick * 0.6) % h;
            gc.setFill(Color.color(1.0, 0.95, 0.3, 0.5));
            gc.fillOval(px, py, 3, 3);
        }
    }

    /** Lá rụng mùa Thu — 30 hạt cam/vàng nhỏ */
    private void drawLeaves() {
        int n = 30;
        double w = getWidth(), h = getHeight();
        Color[] leafColors = {
            Color.color(0.9, 0.5, 0.1, 0.6),
            Color.color(0.85, 0.35, 0.05, 0.55),
            Color.color(0.95, 0.7, 0.1, 0.5),
        };
        for (int i = 0; i < n; i++) {
            double seed = (i * 113.7 + frameTick * 0.55) % w;
            double px   = seed % w;
            double py   = (i * 57 + frameTick * 0.9) % h;
            gc.setFill(leafColors[i % leafColors.length]);
            // Lá hình elip xoay nhẹ
            gc.save();
            gc.translate(px, py);
            gc.rotate((frameTick * 0.5 + i * 30) % 360);
            gc.fillOval(-4, -2, 8, 4);
            gc.restore();
        }
    }

    // ── Selection & Popup ─────────────────────────────────────────────────────

    private Animal findAnimalAt(double mx, double my) {
        if (lastMap == null) return null;
        for (Entity e : lastMap.getEntities()) {
            if (!(e instanceof Animal) || !e.isAlive()) continue;
            double dx = e.getX() - mx, dy = e.getY() - my;
            if (dx * dx + dy * dy <= (e.getSize() * 0.7 + 6) * (e.getSize() * 0.7 + 6))
                return (Animal) e;
        }
        return null;
    }

    private void drawSelectionRing() {
        double r = selectedAnimal.getSize() * 0.75 + 7;
        // Outer glow
        gc.setStroke(Color.color(1, 1, 0, 0.3));
        gc.setLineWidth(5);
        gc.strokeOval(selectedAnimal.getX() - r, selectedAnimal.getY() - r, r * 2, r * 2);
        // Inner ring
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(1.5);
        gc.strokeOval(selectedAnimal.getX() - r, selectedAnimal.getY() - r, r * 2, r * 2);
        gc.setLineWidth(1);
    }

    private void drawInfoPopup() {
        String   info  = selectedAnimal.getInfo();
        String[] lines = info.split(" \\| ");

        double px  = selectedAnimal.getX();
        double py  = selectedAnimal.getY() - selectedAnimal.getSize() * 0.75 - 16;
        double boxW = 220;
        double lineH = 14;
        double boxH  = lineH * lines.length + 12;
        double boxX  = Math.max(2, Math.min(px - boxW / 2, getWidth()  - boxW - 2));
        double boxY  = Math.max(2, py - boxH);

        // Nền popup
        gc.setFill(Color.color(0.05, 0.05, 0.15, 0.82));
        gc.fillRoundRect(boxX, boxY, boxW, boxH, 8, 8);
        gc.setStroke(Color.color(1, 1, 0, 0.7));
        gc.setLineWidth(0.8);
        gc.strokeRoundRect(boxX, boxY, boxW, boxH, 8, 8);

        // Text từng dòng
        gc.setFont(new Font("Arial", 11));
        for (int i = 0; i < lines.length; i++) {
            // Dòng đầu: tên loài — highlight trắng
            gc.setFill(i == 0 ? Color.WHITE : Color.color(0.8, 0.85, 1.0));
            gc.fillText(lines[i], boxX + 8, boxY + 13 + i * lineH);
        }
        gc.setLineWidth(1);
    }

    // ── Overlays ──────────────────────────────────────────────────────────────

    private void drawSpawnOverlay() {
        gc.setFill(Color.color(0, 0, 0, 0.55));
        gc.fillRoundRect(8, 8, 290, 26, 8, 8);
        gc.setFill(Color.color(0.6, 1.0, 0.6));
        gc.setFont(new Font("Arial", 12));
        gc.fillText("✚  Click lên bản đồ để đặt: " + currentSpawnMode.name()
                  + "   [Esc] để hủy", 16, 26);
    }

    private void drawPauseOverlay() {
        gc.setFill(Color.color(0, 0, 0, 0.42));
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial Bold", 36));
        gc.fillText("⏸  PAUSED", getWidth() / 2 - 90, getHeight() / 2);
        gc.setFont(new Font("Arial", 14));
        gc.setFill(Color.color(1, 1, 1, 0.7));
        gc.fillText("Nhấn Space để tiếp tục", getWidth() / 2 - 80, getHeight() / 2 + 30);
    }

    // ── Setters / Getters ─────────────────────────────────────────────────────

    public void setOnMapClick(BiConsumer<Double, Double> handler) { this.onMapClick = handler; }
    public void setSpawnMode(SpawnMode mode) { this.currentSpawnMode = mode; }
    public SpawnMode getSpawnMode()          { return currentSpawnMode; }
    public void setPaused(boolean paused)    { this.paused = paused; }
}
