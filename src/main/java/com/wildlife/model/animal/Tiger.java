package com.wildlife.model.animal;

import com.wildlife.strategy.HunterStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tiger extends Animal {

    public Tiger(double x, double y) {
        super(x, y, 30, 2.8, 200);
        this.visionRange = 250;
        this.setStrategy(new HunterStrategy());
    }

    @Override
    protected Animal createOffspring(double x, double y) {
        return new Tiger(x, y);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) {
            double alpha = getDeathAlpha();
            if (alpha <= 0) return;
            gc.setGlobalAlpha(alpha * 0.4);
            gc.setFill(Color.ORANGE);
            gc.fillRect(x - size / 2, y - size / 2, size, size);
            gc.setGlobalAlpha(1.0);
            return;
        }

        // Thân cam
        gc.setFill(Color.web("#e67e00"));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.fillRect(x - size / 2, y - size / 2, size, size * 0.8);
        gc.strokeRect(x - size / 2, y - size / 2, size, size * 0.8);

        // Sọc đen
        gc.setStroke(Color.web("#1a1a1a"));
        gc.setLineWidth(2.5);
        gc.strokeLine(x - size / 4, y - size / 2, x - size / 4, y + size * 0.3);
        gc.strokeLine(x,            y - size / 2, x,            y + size * 0.3);
        gc.strokeLine(x + size / 4, y - size / 2, x + size / 4, y + size * 0.3);

        // Đầu
        gc.setFill(Color.web("#e67e00"));
        gc.setLineWidth(1);
        gc.fillOval(x - size / 2.5, y - size / 2 - size * 0.35, size * 0.8, size * 0.55);
        gc.strokeOval(x - size / 2.5, y - size / 2 - size * 0.35, size * 0.8, size * 0.55);

        // Tai nhọn
        gc.setFill(Color.web("#cc6600"));
        gc.fillPolygon(
            new double[]{x - size/3, x - size/2 + 2, x - size/6},
            new double[]{y - size/2 - size*0.3, y - size/2 - size*0.6, y - size/2 - size*0.55},
            3);
        gc.fillPolygon(
            new double[]{x + size/3, x + size/2 - 2, x + size/6},
            new double[]{y - size/2 - size*0.3, y - size/2 - size*0.6, y - size/2 - size*0.55},
            3);

        // Mắt vàng
        gc.setFill(Color.web("#f0c000"));
        gc.fillOval(x - size / 4, y - size / 2 - size * 0.15, 5, 5);
        gc.fillOval(x + size / 8, y - size / 2 - size * 0.15, 5, 5);
        gc.setFill(Color.BLACK);
        gc.fillOval(x - size / 4 + 1.5, y - size / 2 - size * 0.15 + 1.5, 2, 2);
        gc.fillOval(x + size / 8 + 1.5, y - size / 2 - size * 0.15 + 1.5, 2, 2);
        gc.setLineWidth(1);

        renderStatusBars(gc);
        renderStateLabel(gc);
    }
}
