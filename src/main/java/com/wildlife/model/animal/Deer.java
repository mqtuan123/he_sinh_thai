package com.wildlife.model.animal;

import com.wildlife.strategy.ScaredStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Deer extends Animal {
    public Deer(double x, double y) {
        // Tốc độ 2.2, nhanh hơn thỏ nhưng chậm hơn sói. Kích thước 20
        super(x, y, 20, 2.2);
        this.visionRange = 150;
        this.setStrategy(new ScaredStrategy());
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;
        gc.setFill(Color.web("#CD853F")); // Màu nâu sáng
        gc.setStroke(Color.BLACK);
        
        // Thân hình
        gc.fillOval(x - size / 2, y - size / 2, size, size);
        gc.strokeOval(x - size / 2, y - size / 2, size, size);
        
        // Sừng hươu
        gc.setStroke(Color.SADDLEBROWN);
        gc.setLineWidth(2);
        gc.strokeLine(x - 5, y - size/2, x - 10, y - size);
        gc.strokeLine(x + 5, y - size/2, x + 10, y - size);
        gc.setLineWidth(1);
    }
}
