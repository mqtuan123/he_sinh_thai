package com.wildlife.view;

import com.wildlife.model.animal.*;
import com.wildlife.model.base.Entity;
import com.wildlife.model.enums.Season;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.observer.EventPublisher;
import com.wildlife.observer.GameEventListener;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * HUD bottom bar — hiển thị:
 *  FPS | Mùa | Số lượng từng loài (icon × số) | Cảnh báo mùa
 * Tự cập nhật qua Observer khi có thay đổi entity/mùa.
 * Fix: unsubscribeAll() khi không còn dùng (gọi dispose() khi đổi bản đồ).
 */
public class HUDView extends HBox implements GameEventListener {

    private final Label fpsLabel;
    private final Label seasonLabel;
    private final Label populationLabel;
    private final Label warningLabel;
    private final WorldMap map;

    public HUDView(WorldMap map) {
        this.map = map;
        this.setPrefHeight(40);
        this.setPadding(new Insets(5, 14, 5, 14));
        this.setSpacing(20);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle("-fx-background-color: #12121e;");

        Font mono  = new Font("Consolas", 12);
        Font norm  = new Font("Arial", 12);

        fpsLabel        = styledLabel("FPS: —",          mono,  "#aaaaaa");
        seasonLabel     = styledLabel("",                 norm,  "#ffffff");
        populationLabel = styledLabel("",                 norm,  "#dddddd");
        warningLabel    = styledLabel("",                 norm,  "#ffdd44");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(fpsLabel, seasonLabel, populationLabel, spacer, warningLabel);

        // Subscribe
        EventPublisher.getInstance().subscribe("ENTITY_ADDED",   this);
        EventPublisher.getInstance().subscribe("ENTITY_REMOVED", this);
        EventPublisher.getInstance().subscribe("SEASON_CHANGED", this);

        updateSeason(map.getCurrentSeason());
        updatePopulation();
    }

    public void updateFPS(int fps) {
        Platform.runLater(() -> {
            String color = fps >= 55 ? "#55cc55" : fps >= 30 ? "#ffaa00" : "#ff4444";
            fpsLabel.setText("FPS: " + fps);
            fpsLabel.setTextFill(Color.web(color));
        });
    }

    @Override
    public void onEvent(String eventType) {
        Platform.runLater(() -> {
            switch (eventType) {
                case "ENTITY_ADDED":
                case "ENTITY_REMOVED":
                    updatePopulation();
                    break;
                case "SEASON_CHANGED":
                    updateSeason(map.getCurrentSeason());
                    break;
            }
        });
    }

    private void updateSeason(Season s) {
        seasonLabel.setText(seasonEmoji(s));
        warningLabel.setText(seasonWarning(s));
    }

    private void updatePopulation() {
        long rabbits   = count(Rabbit.class);
        long deer      = count(Deer.class);
        long wolves    = count(Wolf.class);
        long tigers    = count(Tiger.class);
        long elephants = count(Elephant.class);
        long ducks     = count(com.wildlife.model.animal.Duck.class);
        long total     = rabbits + deer + wolves + tigers + elephants + ducks;

        StringBuilder sb = new StringBuilder("🐾 ").append(total).append("  ");
        if (rabbits   > 0) sb.append("🐰×").append(rabbits).append("  ");
        if (deer      > 0) sb.append("🦌×").append(deer).append("  ");
        if (wolves    > 0) sb.append("🐺×").append(wolves).append("  ");
        if (tigers    > 0) sb.append("🐯×").append(tigers).append("  ");
        if (elephants > 0) sb.append("🐘×").append(elephants).append("  ");
        if (ducks     > 0) sb.append("🦆×").append(ducks);

        populationLabel.setText(sb.toString().trim());
    }

    private long count(Class<?> cls) {
        return map.getEntities().stream()
            .filter(e -> cls.isInstance(e) && e.isAlive())
            .count();
    }

    private String seasonEmoji(Season s) {
        switch (s) {
            case SPRING: return "🌸 Mùa Xuân";
            case SUMMER: return "☀️  Mùa Hè";
            case AUTUMN: return "🍂 Mùa Thu";
            case WINTER: return "❄️  Mùa Đông";
            default: return s.name();
        }
    }

    private String seasonWarning(Season s) {
        switch (s) {
            case SUMMER: return "⚠ Khát tăng 50% — tìm hồ!";
            case WINTER: return "⚠ Ở ngoài trời mất HP — vào rừng!";
            case AUTUMN: return "🍂 Cây cối tàn dần...";
            case SPRING: return "🌱 Mùa sinh sản — dân số tăng";
            default: return "";
        }
    }

    /** Unsubscribe khi HUD bị replace (đổi bản đồ) — tránh memory leak */
    public void dispose() {
        EventPublisher.getInstance().unsubscribeAll(this);
    }

    private Label styledLabel(String text, Font font, String hexColor) {
        Label lbl = new Label(text);
        lbl.setFont(font);
        lbl.setTextFill(Color.web(hexColor));
        return lbl;
    }
}
