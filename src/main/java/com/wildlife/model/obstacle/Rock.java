package com.wildlife.model.obstacle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Rock extends Obstacle {
    public Rock(double x, double y) {
        super(x, y, 30); // Tảng đá khá to, size 30
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;
        gc.setFill(Color.DARKGRAY);
        gc.setStroke(Color.BLACK);
        
        // Vẽ tảng đá có hình dạng hơi méo một chút
        double[] xPoints = {x - size/2, x - size/4, x + size/3, x + size/2, x + size/4, x - size/3};
        double[] yPoints = {y, y - size/2, y - size/3, y + size/4, y + size/2, y + size/3};
        
        gc.fillPolygon(xPoints, yPoints, 6);
        gc.strokePolygon(xPoints, yPoints, 6);
    }
}
