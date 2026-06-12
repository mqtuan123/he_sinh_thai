package com.wildlife.controller;

import com.wildlife.app.Config;
import com.wildlife.engine.GameLoop;
import com.wildlife.factory.AnimalFactory;
import com.wildlife.factory.ObstacleFactory;
import com.wildlife.factory.PlantFactory;
import com.wildlife.model.base.Entity;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.view.ControlPanel;
import com.wildlife.view.GameView;
import com.wildlife.view.HUDView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller chính điều phối Model, View và Engine.
 */
public class GameController {
    private Stage stage;
    private WorldMap worldMap;
    private GameView gameView;
    private HUDView hudView;
    private ControlPanel controlPanel;
    
    private AnimalFactory animalFactory;
    private PlantFactory plantFactory;
    private ObstacleFactory obstacleFactory;

    public GameController(Stage stage) {
        this.stage = stage;
        this.worldMap = new WorldMap(Config.MAP_WIDTH, Config.MAP_HEIGHT);
        this.gameView = new GameView();
        this.hudView = new HUDView(worldMap);
        this.controlPanel = new ControlPanel(this, gameView);
        
        this.animalFactory = new AnimalFactory();
        this.plantFactory = new PlantFactory();
        this.obstacleFactory = new ObstacleFactory();

        this.gameView.setController(this);
    }

    public void start() {
        BorderPane root = new BorderPane();
        root.setCenter(gameView);
        root.setBottom(hudView);
        root.setRight(controlPanel);

        Scene scene = new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        stage.setTitle("Wild-Life Eco Simulation");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Khởi tạo Game Loop
        GameLoop loop = GameLoop.getInstance();
        loop.initialize(worldMap, gameView);
        loop.start();
        
        // Timer riêng để update FPS trên HUD mỗi giây
        Timeline fpsTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            hudView.updateFPS(loop.getCurrentFPS());
        }));
        fpsTimer.setCycleCount(Timeline.INDEFINITE);
        fpsTimer.play();
        
        // Timer tự động chuyển mùa mỗi 15 giây
        Timeline seasonTimer = new Timeline(new KeyFrame(Duration.seconds(15), e -> {
            worldMap.nextSeason();
        }));
        seasonTimer.setCycleCount(Timeline.INDEFINITE);
        seasonTimer.play();
        
        // Spawn khởi tạo (10 cỏ, 5 thỏ, 1 sói, 3 hòn đá)
        for (int i=0; i<3; i++) spawnObstacle("rock");
        for (int i=0; i<10; i++) spawnPlant("grass");
        for (int i=0; i<5; i++) spawnAnimal("rabbit");
        spawnAnimal("wolf");
    }

    public void spawnAnimal(String type) {
        double x = Math.random() * Config.MAP_WIDTH;
        double y = Math.random() * Config.MAP_HEIGHT;
        Entity animal = animalFactory.createEntity(type, x, y);
        worldMap.addEntity(animal);
    }
    
    public void spawnPlant(String type) {
        double x = Math.random() * Config.MAP_WIDTH;
        double y = Math.random() * Config.MAP_HEIGHT;
        Entity plant = plantFactory.createEntity(type, x, y);
        worldMap.addEntity(plant);
    }
    
    public void spawnObstacle(String type) {
        double x = Math.random() * Config.MAP_WIDTH;
        double y = Math.random() * Config.MAP_HEIGHT;
        Entity obstacle = obstacleFactory.createEntity(type, x, y);
        worldMap.addEntity(obstacle);
    }

    // ===== Click-to-Place: spawn tại tọa độ chỉ định =====

    public void spawnAnimalAt(String type, double x, double y) {
        Entity animal = animalFactory.createEntity(type, x, y);
        worldMap.addEntity(animal);
    }

    public void spawnPlantAt(String type, double x, double y) {
        Entity plant = plantFactory.createEntity(type, x, y);
        worldMap.addEntity(plant);
    }

    public void spawnObstacleAt(String type, double x, double y) {
        Entity obstacle = obstacleFactory.createEntity(type, x, y);
        worldMap.addEntity(obstacle);
    }
    
    public void togglePause() {
        if (GameLoop.getInstance().isPaused()) {
            GameLoop.getInstance().resume();
        } else {
            GameLoop.getInstance().pause();
        }
    }
    
    public void nextSeason() {
        worldMap.nextSeason();
    }
    
    public void changeMap(String mapType) {
        worldMap.loadMapType(mapType);
        
        // Spawn lại sinh vật sau khi đổi map để tránh map trống
        if (mapType.equals("Grassland")) {
            for (int i=0; i<15; i++) spawnPlant("grass");
            for (int i=0; i<5; i++) spawnAnimal("rabbit");
        } else if (mapType.equals("Forest")) {
            for (int i=0; i<10; i++) spawnPlant("tree");
            for (int i=0; i<3; i++) spawnAnimal("wolf");
            for (int i=0; i<5; i++) spawnObstacle("rock");
        } else if (mapType.equals("Lake")) {
            for (int i=0; i<5; i++) spawnPlant("grass");
            for (int i=0; i<2; i++) spawnAnimal("rabbit");
            for (int i=0; i<1; i++) spawnAnimal("wolf");
        } else {
            // Combined
            for (int i=0; i<3; i++) spawnObstacle("rock");
            for (int i=0; i<10; i++) spawnPlant("grass");
            for (int i=0; i<5; i++) spawnAnimal("rabbit");
            spawnAnimal("wolf");
        }
    }
}
