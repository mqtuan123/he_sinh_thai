package com.wildlife.view;

import com.wildlife.app.Config;
import com.wildlife.controller.GameController;
import com.wildlife.model.base.Entity;
import com.wildlife.model.enums.SpawnMode;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.environment.Zone;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Đảm nhiệm việc vẽ (render) bản đồ và thực thể.
 * Hỗ trợ Click-to-Place: click chuột để đặt thực thể tại vị trí click.
 */
public class GameView extends Canvas {
    private GraphicsContext gc;
    private SpawnMode spawnMode = SpawnMode.NONE;
    private GameController controller;

    public GameView() {
        super(Config.MAP_WIDTH, Config.MAP_HEIGHT);
        this.gc = this.getGraphicsContext2D();

        this.setOnMouseClicked(e -> {
            if (spawnMode == SpawnMode.NONE || controller == null) return;
            double x = e.getX();
            double y = e.getY();
            switch (spawnMode) {
                case RABBIT:   controller.spawnAnimalAt("rabbit", x, y); break;
                case WOLF:     controller.spawnAnimalAt("wolf", x, y); break;
                case DEER:     controller.spawnAnimalAt("deer", x, y); break;
                case TIGER:    controller.spawnAnimalAt("tiger", x, y); break;
                case ELEPHANT: controller.spawnAnimalAt("elephant", x, y); break;
                case GRASS:    controller.spawnPlantAt("grass", x, y); break;
                case TREE:     controller.spawnPlantAt("tree", x, y); break;
                case ROCK:     controller.spawnObstacleAt("rock", x, y); break;
                case BUSH:     controller.spawnObstacleAt("bush", x, y); break;
                default: break;
            }
        });
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void setSpawnMode(SpawnMode mode) {
        this.spawnMode = mode;
    }

    public SpawnMode getSpawnMode() {
        return spawnMode;
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
