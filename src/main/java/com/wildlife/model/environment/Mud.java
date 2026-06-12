package com.wildlife.model.environment;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Vùng Bùn — minh họa tính Extensibility của hệ thống địa hình.
 * Thêm loại địa hình mới chỉ cần: kế thừa Zone, override getSpeedModifier() và render().
 * Không cần sửa bất kỳ class nào khác — Animal.move() tự lấy modifier qua đa hình.
 */
public class Mud extends Zone {

    // Vũng bùn cố định
    private final double[] pudX;
    private final double[] pudY;
    private final double[] pudR;
    private static final int PUD_COUNT = 12;

    public Mud(double x, double y, double width, double height) {
        super(x, y, width, height);
        pudX = new double[PUD_COUNT];
        pudY = new double[PUD_COUNT];
        pudR = new double[PUD_COUNT];
        java.util.Random rng = new java.util.Random();
        for (int i = 0; i < PUD_COUNT; i++) {
            pudX[i] = x + 8 + rng.nextDouble() * (width  - 16);
            pudY[i] = y + 8 + rng.nextDouble() * (height - 16);
            pudR[i] = 6  + rng.nextDouble() * 10;
        }
    }

    /** Bùn: chậm hơn 40% so với bình thường */
    @Override
    public double getSpeedModifier() { return 0.6; }

    @Override
    public String getName() { return "Vùng Bùn"; }

    @Override
    public void render(GraphicsContext gc) {
        // Nền nâu xám
        gc.setFill(Color.web("#6b4c2a"));
        gc.fillRect(x, y, width, height);

        // Texture bùn — vết nứt ngẫu nhiên (cố định)
        gc.setStroke(Color.web("#4a3118", 0.5));
        gc.setLineWidth(0.8);
        for (int i = 0; i < PUD_COUNT / 2; i++) {
            gc.strokeLine(pudX[i], pudY[i], pudX[i] + 12, pudY[i] + 5);
        }

        // Vũng nước nhỏ trong bùn
        gc.setFill(Color.web("#4a6741", 0.6));
        for (int i = 0; i < PUD_COUNT; i++) {
            gc.fillOval(pudX[i] - pudR[i], pudY[i] - pudR[i] / 2,
                        pudR[i] * 2, pudR[i]);
        }

        // Viền
        gc.setStroke(Color.web("#3e2b0e", 0.7));
        gc.setLineWidth(1.5);
        gc.strokeRect(x, y, width, height);
        gc.setLineWidth(1);

        // Label
        gc.setFill(Color.web("#d2a679", 0.8));
        gc.setFont(new javafx.scene.text.Font("Arial", 10));
        gc.fillText("🟫 Vùng Bùn", x + 5, y + 14);
    }
}
