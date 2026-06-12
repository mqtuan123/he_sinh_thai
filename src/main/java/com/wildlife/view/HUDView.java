package com.wildlife.view;

import com.wildlife.app.Config;
import com.wildlife.engine.GameLoop;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.observer.EventPublisher;
import com.wildlife.observer.GameEventListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 * Hiển thị thông số (FPS, Mùa, Số lượng động vật, Cảnh báo theo mùa).
 * Dùng Observer Pattern để lắng nghe sự kiện thay vì liên tục kiểm tra (polling).
 */
public class HUDView extends HBox implements GameEventListener {
    private Label fpsLabel;
    private Label populationLabel;
    private Label seasonLabel;
    private Label warningLabel;
    
    private WorldMap map;

    public HUDView(WorldMap map) {
        this.map = map;
        this.setPrefHeight(Config.HUD_HEIGHT);
        this.setPadding(new Insets(10));
        this.setSpacing(50);
        this.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        fpsLabel = new Label("FPS: 0");
        fpsLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        fpsLabel.setFont(new Font("Arial", 16));

        populationLabel = new Label("Population: 0");
        populationLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        populationLabel.setFont(new Font("Arial", 16));

        seasonLabel = new Label("Season: " + map.getCurrentSeason());
        seasonLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        seasonLabel.setFont(new Font("Arial", 16));

        warningLabel = new Label("");
        warningLabel.setTextFill(javafx.scene.paint.Color.ORANGE);
        warningLabel.setFont(new Font("Arial", 14));

        this.getChildren().addAll(fpsLabel, populationLabel, seasonLabel, warningLabel);

        // Đăng ký lắng nghe sự kiện
        EventPublisher.getInstance().subscribe("ENTITY_ADDED", this);
        EventPublisher.getInstance().subscribe("ENTITY_REMOVED", this);
        EventPublisher.getInstance().subscribe("SEASON_CHANGED", this);
    }
    
    // Cập nhật FPS độc lập, có thể gọi từ GameLoop hoặc dùng một timer phụ
    public void updateFPS(int fps) {
        fpsLabel.setText("FPS: " + fps);
    }

    @Override
    public void onEvent(String eventType) {
        if (eventType.equals("ENTITY_ADDED") || eventType.equals("ENTITY_REMOVED")) {
            long animalCount = map.getEntities().stream().filter(e -> e instanceof com.wildlife.model.animal.Animal).count();
            populationLabel.setText("Population (Animals): " + animalCount);
        } else if (eventType.equals("SEASON_CHANGED")) {
            seasonLabel.setText("Season: " + map.getCurrentSeason());
            switch (map.getCurrentSeason()) {
                case SUMMER:
                    warningLabel.setText("⚠ Mùa Hè: Động vật khát nước nhanh hơn!");
                    break;
                case WINTER:
                    warningLabel.setText("⚠ Mùa Đông: Động vật ngoài Rừng sẽ mất máu!");
                    break;
                default:
                    warningLabel.setText("");
                    break;
            }
        }
    }
}
