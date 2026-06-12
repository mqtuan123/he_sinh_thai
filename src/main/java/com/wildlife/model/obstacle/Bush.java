package com.wildlife.model.obstacle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bush extends Obstacle {

    public Bush(double x, double y) { super(x, y, 20); }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) return;
        // Bóng
        gc.setFill(Color.color(0, 0, 0, 0.12));
        gc.fillOval(x - size / 2 + 3, y + 2, size, size / 2);

        // Bụi rậm (3 oval lồng nhau tạo độ dày)
        gc.setFill(Color.web("#1a6b35"));
        gc.fillOval(x - size / 2 - 3, y - size / 2 + 5, size, size - 4);

        gc.setFill(Color.web("#2E8B57"));
        gc.fillOval(x - size / 2,     y - size / 2,     size,      size);
        gc.fillOval(x - size / 2 + 5, y - size / 2 - 5, size - 10, size);
        gc.fillOval(x - size / 2 - 5, y - size / 2 + 5, size,      size - 10);

        // Highlight lá
        gc.setFill(Color.web("#3cb371", 0.5));
        gc.fillOval(x - 5, y - size / 2 + 2, size / 2, size / 3);

        // Viền nhẹ
        gc.setStroke(Color.web("#1a5c30", 0.6));
        gc.setLineWidth(0.8);
        gc.strokeOval(x - size / 2, y - size / 2, size, size);
        gc.setLineWidth(1);
    }
}
