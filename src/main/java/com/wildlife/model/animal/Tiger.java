package com.wildlife.model.animal;

import com.wildlife.strategy.HunterStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tiger extends Animal {
    public Tiger(double x, double y) {
        // Tốc độ 2.8, nhanh nhất. Kích thước 30
        super(x, y, 30, 2.8);
        this.visionRange = 250;
        this.setStrategy(new HunterStrategy());
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;
        gc.setFill(Color.ORANGE);
        gc.setStroke(Color.BLACK);
        
        // Vẽ thân hình
        gc.fillRect(x - size / 2, y - size / 2, size, size);
        gc.strokeRect(x - size / 2, y - size / 2, size, size);
        
        // Vẽ các vằn đen
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(x - size/4, y - size/2, x - size/4, y + size/2);
        gc.strokeLine(x, y - size/2, x, y + size/2);
        gc.strokeLine(x + size/4, y - size/2, x + size/4, y + size/2);
        gc.setLineWidth(1);
    }
}
