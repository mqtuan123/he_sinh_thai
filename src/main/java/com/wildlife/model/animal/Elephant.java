package com.wildlife.model.animal;

import com.wildlife.strategy.ElephantStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Voi — động vật đầu bảng. HP=500, size=50, speed=1.0.
 * Animal.move() tự tạo lực đẩy ×4 → cơ chế "nhường đường".
 */
public class Elephant extends Animal {

    public Elephant(double x, double y) {
        super(x, y, 50, 1.0, 500);
        this.visionRange = 150;
        this.setStrategy(new ElephantStrategy());
    }

    @Override
    protected Animal createOffspring(double x, double y) {
        return new Elephant(x, y);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) {
            double alpha = getDeathAlpha();
            if (alpha <= 0) return;
            gc.setGlobalAlpha(alpha * 0.4);
            gc.setFill(Color.web("#808080"));
            gc.fillOval(x - size / 2, y - size / 2, size, size);
            gc.setGlobalAlpha(1.0);
            return;
        }

        gc.setFill(Color.web("#808080"));
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(2);

        // Thân (hình oval to)
        gc.fillOval(x - size / 2, y - size / 2, size, size * 0.85);
        gc.strokeOval(x - size / 2, y - size / 2, size, size * 0.85);

        // Đầu
        gc.setFill(Color.web("#909090"));
        gc.fillOval(x - size / 3, y - size / 2 - size * 0.25, size * 0.67, size * 0.5);
        gc.strokeOval(x - size / 3, y - size / 2 - size * 0.25, size * 0.67, size * 0.5);

        // Tai lớn
        gc.setFill(Color.web("#9a9a9a"));
        gc.fillOval(x - size / 2 - 12, y - size * 0.15, 22, 32);
        gc.fillOval(x + size / 2 - 10, y - size * 0.15, 22, 32);
        gc.strokeOval(x - size / 2 - 12, y - size * 0.15, 22, 32);
        gc.strokeOval(x + size / 2 - 10, y - size * 0.15, 22, 32);

        // Vòi (đường cong)
        gc.setStroke(Color.web("#707070"));
        gc.setLineWidth(8);
        gc.strokeLine(x, y - size / 2 - size * 0.2, x, y - size / 2 - size * 0.5);
        gc.setLineWidth(6);
        gc.strokeLine(x, y - size / 2 - size * 0.5, x + 8, y - size / 2 - size * 0.62);

        // Ngà (trắng ngà)
        gc.setStroke(Color.web("#FFF8DC"));
        gc.setLineWidth(3);
        gc.strokeLine(x - 6, y - size / 2 - size * 0.05, x - 16, y - size / 2 - size * 0.18);
        gc.strokeLine(x + 6, y - size / 2 - size * 0.05, x + 16, y - size / 2 - size * 0.18);

        // Chân
        gc.setFill(Color.web("#757575"));
        gc.fillRect(x - size / 2 + 3,  y + size * 0.3, 10, size * 0.22);
        gc.fillRect(x - size / 2 + 16, y + size * 0.3, 10, size * 0.22);
        gc.fillRect(x + size / 2 - 13, y + size * 0.3, 10, size * 0.22);
        gc.fillRect(x + size / 2 - 26, y + size * 0.3, 10, size * 0.22);

        // Mắt
        gc.setFill(Color.BLACK);
        gc.fillOval(x - size / 5, y - size / 2 - size * 0.1, 5, 5);
        gc.fillOval(x + size / 8, y - size / 2 - size * 0.1, 5, 5);
        gc.setLineWidth(1);

        renderStatusBars(gc);
        renderStateLabel(gc);
    }
}
