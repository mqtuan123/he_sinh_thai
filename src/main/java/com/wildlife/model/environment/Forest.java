package com.wildlife.model.environment;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Forest extends Zone {

    // Tọa độ cây được sinh 1 lần ở constructor — không dùng Math.random() trong render()
    private final double[] treeX;
    private final double[] treeY;
    private final double[] treeSize;
    private static final int TREE_COUNT = 14;

    public Forest(double x, double y, double width, double height) {
        super(x, y, width, height);
        treeX    = new double[TREE_COUNT];
        treeY    = new double[TREE_COUNT];
        treeSize = new double[TREE_COUNT];
        java.util.Random rng = new java.util.Random();
        for (int i = 0; i < TREE_COUNT; i++) {
            treeX[i]    = x + 10 + rng.nextDouble() * (width  - 20);
            treeY[i]    = y + 10 + rng.nextDouble() * (height - 20);
            treeSize[i] = 12 + rng.nextDouble() * 10; // size 12–22
        }
    }

    /** Rừng rậm: di chuyển chậm hơn 30% */
    @Override
    public double getSpeedModifier() { return 0.7; }

    @Override
    public void render(GraphicsContext gc) {
        // Nền rừng
        gc.setFill(Color.web("#3b592d"));
        gc.fillRect(x, y, width, height);

        // Cỏ nền mờ
        gc.setFill(Color.web("#4a7038", 0.5));
        for (int i = 0; i < TREE_COUNT; i += 2) {
            gc.fillOval(treeX[i] - treeSize[i], treeY[i] - treeSize[i] / 2,
                        treeSize[i] * 2, treeSize[i]);
        }

        // Thân cây (màu nâu)
        gc.setFill(Color.web("#5C3D1E"));
        for (int i = 0; i < TREE_COUNT; i++) {
            gc.fillRect(treeX[i] - 2, treeY[i], 4, treeSize[i] * 0.8);
        }

        // Tán lá (tam giác xanh đậm)
        gc.setFill(Color.web("#2c7a2c"));
        for (int i = 0; i < TREE_COUNT; i++) {
            double s = treeSize[i];
            double[] px = { treeX[i], treeX[i] - s, treeX[i] + s };
            double[] py = { treeY[i] - s * 1.4, treeY[i], treeY[i] };
            gc.fillPolygon(px, py, 3);
            // Tán lá tầng trên (nhỏ hơn)
            double[] px2 = { treeX[i], treeX[i] - s * 0.7, treeX[i] + s * 0.7 };
            double[] py2 = { treeY[i] - s * 2.0, treeY[i] - s * 0.8, treeY[i] - s * 0.8 };
            gc.setFill(Color.web("#1e6b1e"));
            gc.fillPolygon(px2, py2, 3);
            gc.setFill(Color.web("#2c7a2c"));
        }

        // Viền khu rừng
        gc.setStroke(Color.web("#1a3d10", 0.6));
        gc.setLineWidth(1.5);
        gc.strokeRect(x, y, width, height);
        gc.setLineWidth(1);
    }
}
