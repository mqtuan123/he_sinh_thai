package com.wildlife;

import com.wildlife.controller.GameController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Lớp khởi chạy ứng dụng (Entry point).
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameController controller = new GameController(primaryStage);
        controller.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
