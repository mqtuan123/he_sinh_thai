package com.wildlife.model.obstacle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Rock extends Obstacle {

    public Rock(double x, double y) { super(x, y, 30); }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;
        // Bóng
        gc.setFill(Color.color(0, 0, 0, 0.15));
        gc.fillOval(x - size / 2 + 4, y - size / 2 + 4, size, size * 0.7);
        // Đá chính
        gc.setFill(Color.web("#7f8c8d"));
        double[] xp = { x-size/2, x-size/4, x+size/3, x+size/2, x+size/4, x-size/3 };
        double[] yp = { y, y-size/2, y-size/3, y+size/4, y+size/2, y+size/3 };
        gc.fillPolygon(xp, yp, 6);
        gc.setStroke(Color.web("#5d6d7e"));
        gc.setLineWidth(1.5);
        gc.strokePolygon(xp, yp, 6);
        // Highlight
        gc.setStroke(Color.color(1, 1, 1, 0.3));
        gc.setLineWidth(1);
        gc.strokeLine(x - size / 4, y - size / 3, x + size / 6, y - size / 2.5);
        gc.setLineWidth(1);
    }
}
