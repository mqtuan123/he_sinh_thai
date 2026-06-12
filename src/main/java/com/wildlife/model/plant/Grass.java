package com.wildlife.model.plant;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Grass extends Plant {

    public Grass(double x, double y) { super(x, y, 10, 30); }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;
        // Độ sáng theo lượng dinh dưỡng còn lại
        double ratio = nutritionValue / maxNutrition;
        gc.setFill(Color.color(0.2, 0.6 * ratio + 0.2, 0.1));
        // Vẽ 3 lá cỏ nhỏ
        double cx = x, cy = y;
        gc.fillRect(cx - 1, cy - size / 2, 2, size);
        gc.fillRect(cx - 4, cy - size / 2 + 2, 2, size - 2);
        gc.fillRect(cx + 2, cy - size / 2 + 2, 2, size - 2);
    }
}
