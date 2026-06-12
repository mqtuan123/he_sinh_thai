package com.wildlife.controller;

import com.wildlife.app.Config;
import com.wildlife.engine.GameLoop;
import com.wildlife.factory.AnimalFactory;
import com.wildlife.factory.ObstacleFactory;
import com.wildlife.factory.PlantFactory;
import com.wildlife.model.enums.SpawnMode;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.sound.SoundManager;
import com.wildlife.view.ControlPanel;
import com.wildlife.view.GameView;
import com.wildlife.view.HUDView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller chính — MVC coordinator.
 * Thêm: keyboard shortcuts (Space, S, M, Esc).
 * Thêm: ambient sound on start.
 * Fix: ControlPanel constructor nhận WorldMap cho PopulationChart.
 */
public class GameController {

    private final Stage         stage;
    private final WorldMap      worldMap;
    private final GameView      gameView;
    private final HUDView       hudView;
    private final ControlPanel  controlPanel;

    private final AnimalFactory   animalFactory   = new AnimalFactory();
    private final PlantFactory    plantFactory    = new PlantFactory();
    private final ObstacleFactory obstacleFactory = new ObstacleFactory();

    private Timeline seasonTimer;

    public GameController(Stage stage) {
        this.stage       = stage;
        this.worldMap    = new WorldMap(Config.MAP_WIDTH, Config.MAP_HEIGHT);
        this.gameView    = new GameView();
        this.hudView     = new HUDView(worldMap);
        this.controlPanel = new ControlPanel(this, gameView, worldMap);
    }

    public void start() {
        BorderPane root = new BorderPane();
        root.setCenter(gameView);
        root.setBottom(hudView);
        root.setRight(controlPanel);

        Scene scene = new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        stage.setTitle("Wild-Life Eco Simulation  |  Space=Pause  S=Mùa  M=Âm thanh  Esc=Hủy chế độ");
        stage.setScene(scene);
        stage.setResizable(false);

        // ── Keyboard shortcuts ───────────────────────────────────────────────
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE)  { togglePause();  return; }
            if (e.getCode() == KeyCode.S)      { nextSeason();   return; }
            if (e.getCode() == KeyCode.M)      { toggleSound();  return; }
            if (e.getCode() == KeyCode.ESCAPE) {
                gameView.setSpawnMode(SpawnMode.NONE);
            }
        });

        stage.show();

        // Canvas click → spawn
        gameView.setOnMapClick((x, y) -> spawnAtPosition(gameView.getSpawnMode(), x, y));

        // Game loop
        GameLoop loop = GameLoop.getInstance();
        loop.initialize(worldMap, gameView);
        loop.start();

        // FPS refresh 1Hz
        Timeline fpsTimer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> hudView.updateFPS(loop.getCurrentFPS()))
        );
        fpsTimer.setCycleCount(Timeline.INDEFINITE);
        fpsTimer.play();

        // Tự động chuyển mùa mỗi 20 giây
        seasonTimer = new Timeline(
            new KeyFrame(Duration.seconds(20), e -> worldMap.nextSeason())
        );
        seasonTimer.setCycleCount(Timeline.INDEFINITE);
        seasonTimer.play();

        // Ambient sound
        SoundManager.getInstance().playLooping(SoundManager.SoundEvent.AMBIENT);

        // Seed khởi đầu
        seedInitialEntities();
    }

    // ── Spawn ──────────────────────────────────────────────────────────────────

    public void spawnAtPosition(SpawnMode mode, double x, double y) {
        if (mode == null || mode == SpawnMode.NONE) return;
        switch (mode) {
            case RABBIT:   worldMap.addEntity(animalFactory.createEntity("rabbit",   x, y)); break;
            case DEER:     worldMap.addEntity(animalFactory.createEntity("deer",     x, y)); break;
            case WOLF:     worldMap.addEntity(animalFactory.createEntity("wolf",     x, y)); break;
            case TIGER:    worldMap.addEntity(animalFactory.createEntity("tiger",    x, y)); break;
            case ELEPHANT: worldMap.addEntity(animalFactory.createEntity("elephant", x, y)); break;
            case DUCK:     worldMap.addEntity(animalFactory.createEntity("duck",     x, y)); break;
            case GRASS:    worldMap.addEntity(plantFactory.createEntity("grass",     x, y)); break;
            case TREE:     worldMap.addEntity(plantFactory.createEntity("tree",      x, y)); break;
            case ROCK:     worldMap.addEntity(obstacleFactory.createEntity("rock",   x, y)); break;
            case BUSH:     worldMap.addEntity(obstacleFactory.createEntity("bush",   x, y)); break;
            default: break;
        }
    }

    public void spawnRandom(SpawnMode mode) {
        spawnAtPosition(mode,
            Math.random() * Config.MAP_WIDTH,
            Math.random() * Config.MAP_HEIGHT);
    }

    // ── Map change ────────────────────────────────────────────────────────────

    public void changeMap(String mapType) {
        worldMap.loadMapType(mapType);
        switch (mapType) {
            case "Grassland":
                repeat(15, () -> spawnRandom(SpawnMode.GRASS));
                repeat(6,  () -> spawnRandom(SpawnMode.RABBIT));
                repeat(2,  () -> spawnRandom(SpawnMode.DEER));
                repeat(1,  () -> spawnRandom(SpawnMode.WOLF));
                repeat(4,  () -> spawnRandom(SpawnMode.DUCK));
                break;
            case "Forest":
                repeat(10, () -> spawnRandom(SpawnMode.TREE));
                repeat(6,  () -> spawnRandom(SpawnMode.BUSH));
                repeat(5,  () -> spawnRandom(SpawnMode.ROCK));
                repeat(5,  () -> spawnRandom(SpawnMode.RABBIT));
                repeat(2,  () -> spawnRandom(SpawnMode.WOLF));
                repeat(1,  () -> spawnRandom(SpawnMode.TIGER));
                break;
            case "Lake":
                repeat(5,  () -> spawnRandom(SpawnMode.GRASS));
                repeat(3,  () -> spawnRandom(SpawnMode.RABBIT));
                repeat(1,  () -> spawnRandom(SpawnMode.WOLF));
                repeat(4,  () -> spawnRandom(SpawnMode.DUCK));
                break;
            default:
                seedInitialEntities();
                break;
        }
    }

    // ── Controls ──────────────────────────────────────────────────────────────

    public void togglePause() {
        GameLoop loop = GameLoop.getInstance();
        if (loop.isPaused()) loop.resume(); else loop.pause();
    }

    public void nextSeason() { worldMap.nextSeason(); }

    private void toggleSound() {
        SoundManager sm = SoundManager.getInstance();
        sm.setEnabled(!sm.isEnabled());
        if (sm.isEnabled()) sm.playLooping(SoundManager.SoundEvent.AMBIENT);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void seedInitialEntities() {
        repeat(4,  () -> spawnRandom(SpawnMode.ROCK));
        repeat(5,  () -> spawnRandom(SpawnMode.BUSH));
        repeat(12, () -> spawnRandom(SpawnMode.GRASS));
        repeat(4,  () -> spawnRandom(SpawnMode.TREE));
        repeat(7,  () -> spawnRandom(SpawnMode.RABBIT));
        repeat(3,  () -> spawnRandom(SpawnMode.DEER));
        repeat(2,  () -> spawnRandom(SpawnMode.WOLF));
        repeat(1,  () -> spawnRandom(SpawnMode.TIGER));
        repeat(1,  () -> spawnRandom(SpawnMode.ELEPHANT));
        repeat(3,  () -> spawnRandom(SpawnMode.DUCK));
    }

    private void repeat(int n, Runnable action) {
        for (int i = 0; i < n; i++) action.run();
    }

    public WorldMap getWorldMap() { return worldMap; }
}
