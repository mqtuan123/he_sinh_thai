package com.wildlife.model.environment;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Grassland extends Zone {

    // Tọa độ bông cỏ sinh ngẫu nhiên 1 lần
    private final double[] grassX;
    private final double[] grassY;
    private static final int GRASS_TUFT_COUNT = 30;

    public Grassland(double x, double y, double width, double height) {
        super(x, y, width, height);
        grassX = new double[GRASS_TUFT_COUNT];
        grassY = new double[GRASS_TUFT_COUNT];
        java.util.Random rng = new java.util.Random();
        for (int i = 0; i < GRASS_TUFT_COUNT; i++) {
            grassX[i] = x + rng.nextDouble() * width;
            grassY[i] = y + rng.nextDouble() * height;
        }
    }

    @Override
    public double getSpeedModifier() { return 1.0; }

    @Override
    public void render(GraphicsContext gc) {
        // Nền đồng cỏ
        gc.setFill(Color.web("#a8d8a8"));
        gc.fillRect(x, y, width, height);

        // Bụi cỏ nhỏ điểm xuyết (cố định)
        gc.setFill(Color.web("#7db87d", 0.6));
        for (int i = 0; i < GRASS_TUFT_COUNT; i++) {
            gc.fillOval(grassX[i] - 6, grassY[i] - 3, 12, 6);
        }
    }
}
