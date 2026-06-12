package com.wildlife.model.animal;

import com.wildlife.strategy.DuckStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Vịt — minh chứng Extensibility: thêm loài mới chỉ bằng cách
 * kế thừa Animal và override createOffspring() + render().
 *
 * Đặc điểm: sống tốt trong hồ (không bị giảm tốc ở Lake),
 * nhanh trên nước hơn đất liền.
 */
public class Duck extends Animal {

    public Duck(double x, double y) {
        super(x, y, 16, 1.8, 80);
        this.visionRange = 110;
        this.setStrategy(new DuckStrategy());
    }

    @Override
    protected boolean isAquatic() { return true; }

    @Override
    protected Animal createOffspring(double x, double y) {
        return new Duck(x, y);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) {
            double alpha = getDeathAlpha();
            if (alpha <= 0) return;
            gc.setGlobalAlpha(alpha * 0.4);
            gc.setFill(Color.web("#f0c040"));
            gc.fillOval(x - size / 2, y - size / 2, size, size);
            gc.setGlobalAlpha(1.0);
            return;
        }

        // Thân vàng
        gc.setFill(Color.web("#f0c040"));
        gc.setStroke(Color.web("#c09020"));
        gc.setLineWidth(1);
        gc.fillOval(x - size / 2, y - size / 2, size, size * 0.8);
        gc.strokeOval(x - size / 2, y - size / 2, size, size * 0.8);

        // Đầu xanh
        gc.setFill(Color.web("#1a7a3a"));
        gc.fillOval(x + size / 4 - 3, y - size / 2 - 5, size * 0.55, size * 0.55);
        gc.strokeOval(x + size / 4 - 3, y - size / 2 - 5, size * 0.55, size * 0.55);

        // Mỏ cam
        gc.setFill(Color.web("#ff8800"));
        gc.fillRect(x + size / 2 - 1, y - size / 2 + 1, 7, 4);

        // Mắt
        gc.setFill(Color.BLACK);
        gc.fillOval(x + size / 2 - 4, y - size / 2 - 1, 3, 3);

        renderStatusBars(gc);
        renderStateLabel(gc);
    }
}
