package com.wildlife.model.environment;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Grassland extends Zone {
    public Grassland(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.web("#a8e6cf")); // Màu xanh nhạt (Đồng cỏ)
        gc.fillRect(x, y, width, height);
    }
}
