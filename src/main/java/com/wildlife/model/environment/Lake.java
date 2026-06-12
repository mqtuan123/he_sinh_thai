package com.wildlife.model.environment;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Lake extends Zone {
    public Lake(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public double getSpeedModifier() {
        return 0.5; // Lội nước làm động vật di chuyển chậm hẳn lại
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.web("#5DADE2")); // Màu nước
        gc.fillOval(x, y, width, height); // Hồ hình oval
    }
}
