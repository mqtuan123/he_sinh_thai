package com.wildlife.model.environment;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;

public class Lake extends Zone {

    private final double[] waveX;
    private final double[] waveY;
    private final double[] waveLen;
    private static final int WAVE_COUNT = 8;

    public Lake(double x, double y, double width, double height) {
        super(x, y, width, height);
        waveX   = new double[WAVE_COUNT];
        waveY   = new double[WAVE_COUNT];
        waveLen = new double[WAVE_COUNT];
        java.util.Random rng = new java.util.Random();
        for (int i = 0; i < WAVE_COUNT; i++) {
            waveX[i]   = x + 12 + rng.nextDouble() * (width  - 24);
            waveY[i]   = y + 8  + rng.nextDouble() * (height - 16);
            waveLen[i] = 12 + rng.nextDouble() * 18;
        }
    }

    @Override
    public double getSpeedModifier() { return 0.5; }

    /**
     * Override: Lake vẽ hình oval → contains() kiểm tra theo oval thay vì rect.
     * Công thức: (px - cx)²/rx² + (py - cy)²/ry² <= 1
     */
    @Override
    public boolean contains(double px, double py) {
        double cx = x + width  / 2;
        double cy = y + height / 2;
        double rx = width  / 2;
        double ry = height / 2;
        double dx = (px - cx) / rx;
        double dy = (py - cy) / ry;
        return dx * dx + dy * dy <= 1.0;
    }

    @Override
    public String getName() { return "Hồ nước"; }

    @Override
    public void render(GraphicsContext gc) {
        // Bóng nhẹ
        gc.setFill(Color.color(0, 0, 0, 0.15));
        gc.fillOval(x + 5, y + 5, width, height * 0.6);

        // Thân hồ
        gc.setFill(Color.web("#2980b9"));
        gc.fillOval(x, y, width, height);

        // Lớp sáng trên mặt nước
        gc.setFill(Color.web("#5dade2", 0.4));
        gc.fillOval(x + width * 0.1, y + height * 0.1, width * 0.5, height * 0.35);

        // Sóng nước (cố định, không random mỗi frame)
        gc.setStroke(Color.web("#7fb3d3", 0.65));
        gc.setLineWidth(1.2);
        for (int i = 0; i < WAVE_COUNT; i++) {
            gc.strokeArc(
                waveX[i] - waveLen[i] / 2,
                waveY[i] - 3,
                waveLen[i], 6,
                0, 180, ArcType.OPEN
            );
        }

        // Viền hồ
        gc.setStroke(Color.web("#1a6fa8", 0.8));
        gc.setLineWidth(2);
        gc.strokeOval(x, y, width, height);
        gc.setLineWidth(1);

        // Label
        gc.setFill(Color.web("#d6eaf8", 0.85));
        gc.setFont(new Font("Arial", 10));
        gc.fillText("💧 Hồ nước", x + width / 2 - 28, y + height / 2 + 4);
    }
}
