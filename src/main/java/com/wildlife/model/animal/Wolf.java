package com.wildlife.model.animal;

import com.wildlife.strategy.HunterStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Wolf extends Animal {

    public Wolf(double x, double y) {
        // Tốc độ: 2.5 (Nhanh hơn thỏ một chút để săn), kích thước: 25
        super(x, y, 25, 2.5);
        this.visionRange = 200; // Tầm nhìn xa hơn
        this.setStrategy(new HunterStrategy());
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;
        gc.setFill(Color.GRAY);
        gc.setStroke(Color.DARKGRAY);
        
        // Vẽ hình sói đơn giản (vuông vắn hơn)
        gc.fillRect(x - size / 2, y - size / 2, size, size);
        gc.strokeRect(x - size / 2, y - size / 2, size, size);
        
        // Mắt đỏ (nguy hiểm)
        gc.setFill(Color.RED);
        gc.fillOval(x - size/4, y - size/4, 4, 4);
        gc.fillOval(x + size/8, y - size/4, 4, 4);
    }
}
