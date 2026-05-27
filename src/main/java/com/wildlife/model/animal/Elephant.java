package com.wildlife.model.animal;

import com.wildlife.strategy.PassiveStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Elephant extends Animal {
    public Elephant(double x, double y) {
        // Tốc độ chậm 1.0. Kích thước cực kỳ khổng lồ 50
        super(x, y, 50, 1.0);
        this.visionRange = 150;
        this.health = 500; // Máu cực trâu
        this.setStrategy(new PassiveStrategy()); // Voi chỉ ăn thực vật
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;
        gc.setFill(Color.web("#808080"));
        gc.setStroke(Color.DARKGRAY);
        
        // Vẽ thân hình voi (Hình tròn to)
        gc.fillOval(x - size / 2, y - size / 2, size, size);
        
        // Vẽ vòi voi
        gc.setStroke(Color.web("#808080"));
        gc.setLineWidth(8);
        gc.strokeLine(x, y - size/2, x, y - size/2 - 20);
        gc.setLineWidth(1);
    }
}
