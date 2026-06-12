package com.wildlife.view;

import com.wildlife.app.Config;
import com.wildlife.controller.GameController;
import com.wildlife.model.enums.SpawnMode;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/**
 * Bảng điều khiển thêm/bớt động vật, đổi mùa, và chế độ Click-to-Place.
 */
public class ControlPanel extends VBox {
    private GameController controller;
    private GameView gameView;

    public ControlPanel(GameController controller, GameView gameView) {
        this.controller = controller;
        this.gameView = gameView;
        this.setPrefWidth(Config.CONTROL_PANEL_WIDTH);
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #DDDDDD;");

        Label title = new Label("CONTROL PANEL");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label mapTitle = new Label("Chọn Bản Đồ:");
        ComboBox<String> mapSelector = new ComboBox<>();
        mapSelector.getItems().addAll("Combined", "Grassland", "Forest", "Lake");
        mapSelector.setValue("Combined");
        mapSelector.setMaxWidth(Double.MAX_VALUE);
        mapSelector.setOnAction(e -> {
            controller.changeMap(mapSelector.getValue());
        });

        Button btnAddRabbit = new Button("Thêm Thỏ (Rabbit)");
        btnAddRabbit.setMaxWidth(Double.MAX_VALUE);
        btnAddRabbit.setOnAction(e -> controller.spawnAnimal("rabbit"));
        
        Button btnAddDeer = new Button("Thêm Hươu (Deer)");
        btnAddDeer.setMaxWidth(Double.MAX_VALUE);
        btnAddDeer.setOnAction(e -> controller.spawnAnimal("deer"));

        Button btnAddWolf = new Button("Thêm Sói (Wolf)");
        btnAddWolf.setMaxWidth(Double.MAX_VALUE);
        btnAddWolf.setOnAction(e -> controller.spawnAnimal("wolf"));
        
        Button btnAddTiger = new Button("Thêm Hổ (Tiger)");
        btnAddTiger.setMaxWidth(Double.MAX_VALUE);
        btnAddTiger.setOnAction(e -> controller.spawnAnimal("tiger"));
        
        Button btnAddElephant = new Button("Thêm Voi (Elephant)");
        btnAddElephant.setMaxWidth(Double.MAX_VALUE);
        btnAddElephant.setOnAction(e -> controller.spawnAnimal("elephant"));

        Button btnAddGrass = new Button("Thêm Cỏ (Grass)");
        btnAddGrass.setMaxWidth(Double.MAX_VALUE);
        btnAddGrass.setOnAction(e -> controller.spawnPlant("grass"));
        
        Button btnAddTree = new Button("Thêm Cây (Tree)");
        btnAddTree.setMaxWidth(Double.MAX_VALUE);
        btnAddTree.setOnAction(e -> controller.spawnPlant("tree"));
        
        Button btnAddRock = new Button("Thêm Đá (Rock)");
        btnAddRock.setMaxWidth(Double.MAX_VALUE);
        btnAddRock.setOnAction(e -> controller.spawnObstacle("rock"));
        
        Button btnAddBush = new Button("Thêm Bụi rậm (Bush)");
        btnAddBush.setMaxWidth(Double.MAX_VALUE);
        btnAddBush.setOnAction(e -> controller.spawnObstacle("bush"));
        
        Button btnNextSeason = new Button("Đổi Mùa (Next Season)");
        btnNextSeason.setMaxWidth(Double.MAX_VALUE);
        btnNextSeason.setOnAction(e -> controller.nextSeason());

        Button btnTogglePause = new Button("Pause / Resume");
        btnTogglePause.setMaxWidth(Double.MAX_VALUE);
        btnTogglePause.setOnAction(e -> controller.togglePause());

        // ===== Click-to-Place mode =====
        Label clickModeTitle = new Label("Click-to-Place:");
        clickModeTitle.setStyle("-fx-font-weight: bold;");
        ToggleGroup spawnGroup = new ToggleGroup();

        ToggleButton tbNone     = new ToggleButton("Tắt (Off)");
        ToggleButton tbRabbit   = new ToggleButton("Thỏ");
        ToggleButton tbWolf     = new ToggleButton("Sói");
        ToggleButton tbDeer     = new ToggleButton("Hươu");
        ToggleButton tbTiger    = new ToggleButton("Hổ");
        ToggleButton tbElephant = new ToggleButton("Voi");
        ToggleButton tbGrass    = new ToggleButton("Cỏ");
        ToggleButton tbTree     = new ToggleButton("Cây");
        ToggleButton tbRock     = new ToggleButton("Đá");
        ToggleButton tbBush     = new ToggleButton("Bụi rậm");

        for (ToggleButton tb : new ToggleButton[]{tbNone, tbRabbit, tbWolf, tbDeer,
                tbTiger, tbElephant, tbGrass, tbTree, tbRock, tbBush}) {
            tb.setToggleGroup(spawnGroup);
            tb.setMaxWidth(Double.MAX_VALUE);
        }
        tbNone.setSelected(true);

        tbNone.setOnAction(e -> gameView.setSpawnMode(SpawnMode.NONE));
        tbRabbit.setOnAction(e -> gameView.setSpawnMode(SpawnMode.RABBIT));
        tbWolf.setOnAction(e -> gameView.setSpawnMode(SpawnMode.WOLF));
        tbDeer.setOnAction(e -> gameView.setSpawnMode(SpawnMode.DEER));
        tbTiger.setOnAction(e -> gameView.setSpawnMode(SpawnMode.TIGER));
        tbElephant.setOnAction(e -> gameView.setSpawnMode(SpawnMode.ELEPHANT));
        tbGrass.setOnAction(e -> gameView.setSpawnMode(SpawnMode.GRASS));
        tbTree.setOnAction(e -> gameView.setSpawnMode(SpawnMode.TREE));
        tbRock.setOnAction(e -> gameView.setSpawnMode(SpawnMode.ROCK));
        tbBush.setOnAction(e -> gameView.setSpawnMode(SpawnMode.BUSH));

        this.getChildren().addAll(
            title, 
            mapTitle, mapSelector, 
            btnAddRabbit, btnAddDeer, btnAddWolf, btnAddTiger, btnAddElephant, 
            btnAddGrass, btnAddTree, 
            btnAddRock, btnAddBush,
            btnNextSeason, btnTogglePause,
            clickModeTitle, tbNone, tbRabbit, tbWolf, tbDeer,
            tbTiger, tbElephant, tbGrass, tbTree, tbRock, tbBush
        );
    }
}
