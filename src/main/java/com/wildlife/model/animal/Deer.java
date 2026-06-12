package com.wildlife.model.animal;

import com.wildlife.strategy.ScaredStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Deer extends Animal {

    public Deer(double x, double y) {
        super(x, y, 20, 2.2, 120);
        this.visionRange = 150;
        this.setStrategy(new ScaredStrategy());
    }

    @Override
    protected Animal createOffspring(double x, double y) {
        return new Deer(x, y);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) {
            double alpha = getDeathAlpha();
            if (alpha <= 0) return;
            gc.setGlobalAlpha(alpha * 0.4);
            gc.setFill(Color.web("#CD853F"));
            gc.fillOval(x - size / 2, y - size / 2, size, size);
            gc.setGlobalAlpha(1.0);
            return;
        }

        gc.setFill(Color.web("#CD853F"));
        gc.setStroke(Color.web("#8B5E3C"));
        gc.setLineWidth(1);

        // Thân
        gc.fillOval(x - size / 2, y - size / 2, size, size);
        gc.strokeOval(x - size / 2, y - size / 2, size, size);

        // Sừng
        gc.setStroke(Color.SADDLEBROWN);
        gc.setLineWidth(2);
        gc.strokeLine(x - 5, y - size / 2, x - 10, y - size);
        gc.strokeLine(x - 10, y - size, x - 14, y - size - 6);
        gc.strokeLine(x - 10, y - size, x - 6,  y - size - 6);
        gc.strokeLine(x + 5, y - size / 2, x + 10, y - size);
        gc.strokeLine(x + 10, y - size, x + 14, y - size - 6);
        gc.strokeLine(x + 10, y - size, x + 6,  y - size - 6);

        // Mắt
        gc.setFill(Color.BLACK);
        gc.fillOval(x - 5, y - 3, 3, 3);
        gc.fillOval(x + 3, y - 3, 3, 3);
        gc.setLineWidth(1);

        renderStatusBars(gc);
        renderStateLabel(gc);
    }
}
