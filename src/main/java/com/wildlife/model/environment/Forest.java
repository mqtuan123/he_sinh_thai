package com.wildlife.model.environment;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Forest extends Zone {
    public Forest(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public double getSpeedModifier() {
        return 0.7; // Rừng rậm làm động vật di chuyển chậm hơn
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.web("#3b592d")); // Màu xanh lá đậm (Rừng rậm)
        gc.fillRect(x, y, width, height);
        
        // Vẽ thêm vài nét mô phỏng rừng
        gc.setStroke(Color.web("#2c4222"));
        for (int i = 0; i < 5; i++) {
            gc.strokeOval(x + Math.random() * width, y + Math.random() * height, 20, 20);
        }
    }
}
