package com.wildlife.model.animal;

import com.wildlife.strategy.ScaredStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Rabbit extends Animal {

    public Rabbit(double x, double y) {
        super(x, y, 15, 1.5, 100);
        this.visionRange = 100;
        this.setStrategy(new ScaredStrategy());
    }

    @Override
    protected Animal createOffspring(double x, double y) {
        return new Rabbit(x, y);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) {
            // Xác mờ dần
            double alpha = getDeathAlpha();
            if (alpha <= 0) return;
            gc.setGlobalAlpha(alpha * 0.4);
            gc.setFill(Color.LIGHTGRAY);
            gc.fillOval(x - size / 2, y - size / 2, size, size);
            gc.setGlobalAlpha(1.0);
            return;
        }

        gc.setFill(Color.WHITE);
        gc.setStroke(Color.web("#cccccc"));
        gc.setLineWidth(1);

        // Thân
        gc.fillOval(x - size / 2, y - size / 2, size, size);
        gc.strokeOval(x - size / 2, y - size / 2, size, size);

        // Tai
        gc.setFill(Color.WHITE);
        gc.fillOval(x - size / 2,   y - size,      size / 3, size / 1.5);
        gc.fillOval(x + size / 6,   y - size,      size / 3, size / 1.5);
        gc.setFill(Color.web("#ffb6c1")); // tai trong màu hồng
        gc.fillOval(x - size / 2 + 1, y - size + 2, size / 3 - 2, size / 1.5 - 4);
        gc.fillOval(x + size / 6 + 1, y - size + 2, size / 3 - 2, size / 1.5 - 4);
        gc.setStroke(Color.web("#cccccc"));
        gc.strokeOval(x - size / 2, y - size,      size / 3, size / 1.5);
        gc.strokeOval(x + size / 6, y - size,      size / 3, size / 1.5);

        // Mắt
        gc.setFill(Color.web("#ff6b6b"));
        gc.fillOval(x - 4, y - 2, 3, 3);
        gc.fillOval(x + 2, y - 2, 3, 3);

        // Thanh trạng thái
        renderStatusBars(gc);
        renderStateLabel(gc);
    }
}
