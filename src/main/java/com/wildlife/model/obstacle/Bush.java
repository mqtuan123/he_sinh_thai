package com.wildlife.model.obstacle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bush extends Obstacle {
    public Bush(double x, double y) {
        super(x, y, 20); // Bụi rậm size 20
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;
        gc.setFill(Color.web("#2E8B57")); // SeaGreen
        
        // Vẽ bụi rậm xù xì
        gc.fillOval(x - size/2, y - size/2, size, size);
        gc.fillOval(x - size/2 + 5, y - size/2 - 5, size - 10, size);
        gc.fillOval(x - size/2 - 5, y - size/2 + 5, size, size - 10);
    }
}
