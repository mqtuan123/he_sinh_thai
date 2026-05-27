package com.wildlife.view;

import com.wildlife.app.Config;
import com.wildlife.controller.GameController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Bảng điều khiển thêm/bớt động vật, đổi mùa.
 */
public class ControlPanel extends VBox {
    private GameController controller;

    public ControlPanel(GameController controller) {
        this.controller = controller;
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

        this.getChildren().addAll(
            title, 
            mapTitle, mapSelector, 
            btnAddRabbit, btnAddDeer, btnAddWolf, btnAddTiger, btnAddElephant, 
            btnAddGrass, btnAddTree, 
            btnAddRock, btnAddBush,
            btnNextSeason, btnTogglePause
        );
    }
}
