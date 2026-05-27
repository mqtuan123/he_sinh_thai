package com.wildlife.view;

import com.wildlife.app.Config;
import com.wildlife.model.base.Entity;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.environment.Zone;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Đảm nhiệm việc vẽ (render) bản đồ và thực thể.
 */
public class GameView extends Canvas {
    private GraphicsContext gc;

    public GameView() {
        super(Config.MAP_WIDTH, Config.MAP_HEIGHT);
        this.gc = this.getGraphicsContext2D();
    }

    public void render(WorldMap map) {
        // Xóa màn hình cũ (Màu nền phụ thuộc mùa)
        switch (map.getCurrentSeason()) {
            case SPRING: gc.setFill(Color.web("#dcedc1")); break;
            case SUMMER: gc.setFill(Color.web("#e8f5e9")); break;
            case AUTUMN: gc.setFill(Color.web("#ffd3b6")); break;
            case WINTER: gc.setFill(Color.web("#eceff1")); break;
        }
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Vẽ các vùng địa hình (Đồng cỏ, Rừng, Hồ)
        for (Zone zone : map.getZones()) {
            zone.render(gc);
        }

        // Render tất cả các thực thể
        for (Entity e : map.getEntities()) {
            e.render(gc);
        }
    }
}
