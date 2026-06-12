package com.wildlife.model.environment;

import javafx.scene.canvas.GraphicsContext;

/**
 * Lớp trừu tượng đại diện cho một khu vực địa hình trên bản đồ.
 */
public abstract class Zone {
    protected double x, y, width, height;

    public Zone(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(GraphicsContext gc);

    // Kiểm tra xem một tọa độ có nằm trong vùng này không
    public boolean contains(double targetX, double targetY) {
        return targetX >= x && targetX <= x + width &&
               targetY >= y && targetY <= y + height;
    }

    /**
     * Hệ số tốc độ áp dụng cho động vật khi đứng trong vùng này.
     * Mặc định = 1.0 (Grassland). Forest/Lake override lại.
     */
    public double getSpeedModifier() {
        return 1.0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
