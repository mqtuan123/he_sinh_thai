package com.wildlife.model.plant;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FruitTree extends Plant {

    public FruitTree(double x, double y) {
        // Cây ăn quả to hơn cỏ (size 25) và nhiều dinh dưỡng hơn (60)
        super(x, y, 25, 60);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;
        
        // Thân cây
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(x - 5, y, 10, size);
        
        // Tán lá
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(x - size / 2, y - size / 2, size, size);
        
        // Quả (Màu đỏ)
        gc.setFill(Color.RED);
        gc.fillOval(x - 8, y - 8, 6, 6);
        gc.fillOval(x + 5, y - 5, 6, 6);
        gc.fillOval(x - 2, y + 2, 6, 6);
    }
}
