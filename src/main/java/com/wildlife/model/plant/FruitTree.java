package com.wildlife.model.plant;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FruitTree extends Plant {

    public FruitTree(double x, double y) { super(x, y, 25, 60); }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;

        double ratio = nutritionValue / maxNutrition;

        // Thân
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(x - 5, y, 10, size);

        // Tán lá (màu theo dinh dưỡng còn lại)
        gc.setFill(Color.color(0.1, 0.4 * ratio + 0.15, 0.1));
        gc.fillOval(x - size / 2, y - size / 2, size, size);

        // Quả (chỉ hiện khi còn >= 40% dinh dưỡng)
        if (ratio >= 0.4) {
            gc.setFill(Color.web("#e74c3c"));
            gc.fillOval(x - 8, y - 8, 6, 6);
            gc.fillOval(x + 4, y - 5, 6, 6);
            gc.fillOval(x - 2, y + 3, 6, 6);
        }
    }
}
