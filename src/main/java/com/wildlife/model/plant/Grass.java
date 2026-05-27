package com.wildlife.model.plant;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Grass extends Plant {

    public Grass(double x, double y) {
        // Cỏ có size 10, giá trị dinh dưỡng 30
        super(x, y, 10, 30);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;
        gc.setFill(Color.LIGHTGREEN);
        gc.fillOval(x - size / 2, y - size / 2, size, size);
    }
}
