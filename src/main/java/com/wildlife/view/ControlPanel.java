package com.wildlife.view;

import com.wildlife.app.Config;
import com.wildlife.controller.GameController;
import com.wildlife.model.enums.SpawnMode;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.sound.SoundManager;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * ControlPanel hoàn chỉnh:
 *  - 2 nút rõ ràng mỗi loài: [+ Random] và [✚ Click để đặt]
 *  - Spawn mode indicator màu xanh khi đang chọn
 *  - PopulationChart nhúng bên dưới
 *  - Toggle âm thanh (M)
 *  - Keyboard hint
 */
public class ControlPanel extends VBox {

    private final GameController  controller;
    private final GameView        gameView;
    private final WorldMap        worldMap;
    private       Label           spawnModeLabel;
    private       PopulationChart chart;

    public ControlPanel(GameController controller, GameView gameView, WorldMap worldMap) {
        this.controller = controller;
        this.gameView   = gameView;
        this.worldMap   = worldMap;

        this.setPrefWidth(Config.CONTROL_PANEL_WIDTH);
        this.setPadding(new Insets(8, 6, 8, 6));
        this.setSpacing(3);
        this.setStyle("-fx-background-color: #1e1e2e;");

        chart = new PopulationChart(worldMap);

        // ── Tiêu đề ──────────────────────────────────────────────────────────
        Label title = new Label("🌿 WILDLIFE SIM");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #e0e0e0;");

        // ── Bản đồ ───────────────────────────────────────────────────────────
        Label mapLbl = sectionLabel("── Bản đồ ──");
        ComboBox<String> mapBox = new ComboBox<>();
        mapBox.getItems().addAll("Combined", "Grassland", "Forest", "Lake");
        mapBox.setValue("Combined");
        mapBox.setMaxWidth(Double.MAX_VALUE);
        mapBox.setStyle("-fx-font-size: 11px; -fx-background-color: #2d2d3e; -fx-text-fill: white;");
        mapBox.setOnAction(e -> controller.changeMap(mapBox.getValue()));

        // ── Spawn mode badge ──────────────────────────────────────────────────
        spawnModeLabel = new Label("Chế độ: Không chọn");
        spawnModeLabel.setMaxWidth(Double.MAX_VALUE);
        spawnModeLabel.setWrapText(false);
        applyBadgeStyle(spawnModeLabel, false);

        Button cancelBtn = smallBtn("✕ Hủy (Esc)", "#554444", "#ffaaaa");
        cancelBtn.setOnAction(e -> setSpawnMode(SpawnMode.NONE));

        // ── Điều khiển ───────────────────────────────────────────────────────
        Label ctrlLbl   = sectionLabel("── Điều khiển ──");
        Button btnSeason = wideBtn("🌤 Đổi mùa  [S]");
        Button btnPause  = wideBtn("⏸ Pause / Resume  [Space]");
        Button btnSound  = wideBtn("🔊 Toggle âm thanh  [M]");

        btnSeason.setOnAction(e -> controller.nextSeason());
        btnPause .setOnAction(e -> controller.togglePause());
        btnSound .setOnAction(e -> {
            SoundManager sm = SoundManager.getInstance();
            sm.setEnabled(!sm.isEnabled());
            btnSound.setText(sm.isEnabled() ? "🔊 Âm thanh: BẬT  [M]"
                                            : "🔇 Âm thanh: TẮT  [M]");
            if (sm.isEnabled())
                sm.playLooping(SoundManager.SoundEvent.AMBIENT);
        });

        // ── Hint phím tắt ────────────────────────────────────────────────────
        Label hint = new Label("💡 Click con vật → xem thông tin\n"
                + "   [+] Spawn ngẫu nhiên\n"
                + "   [✚] Click lên bản đồ để đặt");
        hint.setStyle("-fx-font-size: 9px; -fx-text-fill: #666688;");
        hint.setWrapText(true);

        // ── Chart label ───────────────────────────────────────────────────────
        Label chartLbl = sectionLabel("── Biểu đồ dân số ──");

        // ── Build layout ──────────────────────────────────────────────────────
        this.getChildren().addAll(
            title,
            new Separator(),
            mapLbl, mapBox,
            new Separator(),
            spawnModeLabel, cancelBtn,
            new Separator(),
            sectionLabel("── Động vật ──"),
            entityRow("🐰 Thỏ",     SpawnMode.RABBIT),
            entityRow("🦌 Hươu",    SpawnMode.DEER),
            entityRow("🐺 Sói",     SpawnMode.WOLF),
            entityRow("🐯 Hổ",      SpawnMode.TIGER),
            entityRow("🐘 Voi",     SpawnMode.ELEPHANT),
            entityRow("🦆 Vịt",     SpawnMode.DUCK),
            sectionLabel("── Thực vật ──"),
            entityRow("🌿 Cỏ",      SpawnMode.GRASS),
            entityRow("🌳 Cây ăn quả", SpawnMode.TREE),
            sectionLabel("── Vật cản ──"),
            entityRow("🪨 Đá",      SpawnMode.ROCK),
            entityRow("🌱 Bụi rậm", SpawnMode.BUSH),
            new Separator(),
            ctrlLbl,
            btnSeason, btnPause, btnSound, buildVolumeSlider(),
            new Separator(),
            chartLbl,
            chart,
            new Separator(),
            hint
        );
    }

    // ── Hàng entity: [+ Tên loài]  [✚] ──────────────────────────────────────
    private HBox entityRow(String label, SpawnMode mode) {
        Button btnRandom = new Button("+ " + label);
        btnRandom.setPrefWidth(115);
        btnRandom.setStyle("-fx-font-size: 10px; -fx-background-color: #2d2d3e; "
                         + "-fx-text-fill: #dddddd; -fx-background-radius: 4; "
                         + "-fx-border-color: #444466; -fx-border-radius: 4;");
        btnRandom.setOnAction(e -> controller.spawnRandom(mode));

        Button btnClick = new Button("✚");
        btnClick.setPrefWidth(32);
        btnClick.setStyle("-fx-font-size: 12px; -fx-background-color: #2d3d2d; "
                        + "-fx-text-fill: #88dd88; -fx-background-radius: 4; "
                        + "-fx-border-color: #446644; -fx-border-radius: 4;");
        btnClick.setOnAction(e -> {
            SpawnMode cur = gameView.getSpawnMode();
            setSpawnMode(cur == mode ? SpawnMode.NONE : mode);
        });

        HBox row = new HBox(3, btnRandom, btnClick);
        row.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    private void setSpawnMode(SpawnMode mode) {
        gameView.setSpawnMode(mode);
        if (mode == SpawnMode.NONE) {
            spawnModeLabel.setText("Chế độ: Không chọn");
            applyBadgeStyle(spawnModeLabel, false);
        } else {
            spawnModeLabel.setText("✚ Đặt: " + mode.name());
            applyBadgeStyle(spawnModeLabel, true);
        }
    }

    private void applyBadgeStyle(Label lbl, boolean active) {
        lbl.setStyle(active
            ? "-fx-background-color: #1e4d1e; -fx-text-fill: #aaffaa; "
              + "-fx-padding: 3 6; -fx-background-radius: 4; -fx-font-size: 10px;"
            : "-fx-background-color: #2d2d3e; -fx-text-fill: #888888; "
              + "-fx-padding: 3 6; -fx-background-radius: 4; -fx-font-size: 10px;");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private HBox buildVolumeSlider() {
        Label lbl = new Label("🔉");
        lbl.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px;");
        Slider slider = new Slider(0, 1, SoundManager.getInstance().getVolume());
        slider.setPrefWidth(110);
        slider.setStyle("-fx-control-inner-background: #2d2d3e;");
        slider.valueProperty().addListener((obs, old, val) ->
            SoundManager.getInstance().setVolume(val.doubleValue()));
        HBox box = new HBox(4, lbl, slider);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private Button wideBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-font-size: 10px; -fx-background-color: #2d2d3e; "
                 + "-fx-text-fill: #cccccc; -fx-background-radius: 4; "
                 + "-fx-border-color: #444466; -fx-border-radius: 4;");
        return b;
    }

    private Button smallBtn(String text, String bg, String fg) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-font-size: 9px; -fx-background-color: " + bg + "; "
                 + "-fx-text-fill: " + fg + "; -fx-background-radius: 4;");
        return b;
    }

    private Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 9px; -fx-text-fill: #555577;");
        return lbl;
    }
}
