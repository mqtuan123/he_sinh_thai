package com.wildlife.model.animal;

import com.wildlife.strategy.HunterStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Wolf extends Animal {

    public Wolf(double x, double y) {
        super(x, y, 25, 2.5, 150);
        this.visionRange = 200;
        this.setStrategy(new HunterStrategy());
    }

    @Override
    protected Animal createOffspring(double x, double y) {
        return new Wolf(x, y);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) {
            double alpha = getDeathAlpha();
            if (alpha <= 0) return;
            gc.setGlobalAlpha(alpha * 0.4);
            gc.setFill(Color.GRAY);
            gc.fillRect(x - size / 2, y - size / 2, size, size);
            gc.setGlobalAlpha(1.0);
            return;
        }

        // Thân
        gc.setFill(Color.web("#888888"));
        gc.setStroke(Color.web("#555555"));
        gc.setLineWidth(1.5);
        gc.fillRect(x - size / 2, y - size / 2, size, size * 0.75);
        gc.strokeRect(x - size / 2, y - size / 2, size, size * 0.75);

        // Đầu (hơi nhô ra)
        gc.setFill(Color.web("#999999"));
        gc.fillOval(x - size / 3, y - size / 2 - size * 0.3, size * 0.67, size * 0.55);
        gc.strokeOval(x - size / 3, y - size / 2 - size * 0.3, size * 0.67, size * 0.55);

        // Tai
        gc.setFill(Color.web("#777777"));
        gc.fillPolygon(
            new double[]{x - size/4, x - size/3 - 2, x - size/8},
            new double[]{y - size/2 - size*0.25, y - size/2 - size*0.55, y - size/2 - size*0.5},
            3);
        gc.fillPolygon(
            new double[]{x + size/4, x + size/3 + 2, x + size/8},
            new double[]{y - size/2 - size*0.25, y - size/2 - size*0.55, y - size/2 - size*0.5},
            3);

        // Mắt đỏ
        gc.setFill(Color.web("#cc2200"));
        gc.fillOval(x - size / 5, y - size / 2 - size * 0.1, 4, 4);
        gc.fillOval(x + size / 8, y - size / 2 - size * 0.1, 4, 4);

        // Chân
        gc.setFill(Color.web("#777777"));
        gc.fillRect(x - size / 2 + 2, y + size * 0.25 - size/2, 5, size * 0.2);
        gc.fillRect(x - size / 2 + 9, y + size * 0.25 - size/2, 5, size * 0.2);
        gc.fillRect(x + size / 2 - 14, y + size * 0.25 - size/2, 5, size * 0.2);
        gc.fillRect(x + size / 2 - 7,  y + size * 0.25 - size/2, 5, size * 0.2);
        gc.setLineWidth(1);

        renderStatusBars(gc);
        renderStateLabel(gc);
    }
}
