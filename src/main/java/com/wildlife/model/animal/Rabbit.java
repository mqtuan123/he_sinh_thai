package com.wildlife.model.animal;

import com.wildlife.strategy.ScaredStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Rabbit extends Animal {

    public Rabbit(double x, double y) {
        // Tốc độ 1.5, nhỏ gọn
        super(x, y, 15, 1.5);
        this.visionRange = 100;
        this.setStrategy(new ScaredStrategy());
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        
        // Thân hình thỏ
        gc.fillOval(x - size / 2, y - size / 2, size, size);
        gc.strokeOval(x - size / 2, y - size / 2, size, size);
        
        // Tai thỏ
        gc.fillOval(x - size / 2, y - size, size / 3, size / 1.5);
        gc.fillOval(x + size / 6, y - size, size / 3, size / 1.5);
        gc.strokeOval(x - size / 2, y - size, size / 3, size / 1.5);
        gc.strokeOval(x + size / 6, y - size, size / 3, size / 1.5);
    }
}
