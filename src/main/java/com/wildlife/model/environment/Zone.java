package com.wildlife.model.environment;

import javafx.scene.canvas.GraphicsContext;

/**
 * Lớp trừu tượng đại diện cho một khu vực địa hình.
 * Thêm: getName() — dùng cho tooltip và log.
 * Subclass override containsPoint() nếu hình dạng không phải rectangle (ví dụ Lake = oval).
 */
public abstract class Zone {

    protected double x, y, width, height;

    public Zone(double x, double y, double width, double height) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
    }

    public abstract void render(GraphicsContext gc);

    /**
     * Hệ số tốc độ di chuyển trong vùng này.
     * 1.0 = bình thường, < 1.0 = chậm, > 1.0 = nhanh.
     */
    public double getSpeedModifier() { return 1.0; }

    /**
     * Mặc định: hình chữ nhật.
     * Lake override thành hình oval để chính xác hơn.
     */
    public boolean contains(double px, double py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    /** Tên hiển thị — dùng trong HUD tooltip */
    public String getName() { return getClass().getSimpleName(); }

    public double getX()      { return x; }
    public double getY()      { return y; }
    public double getWidth()  { return width; }
    public double getHeight() { return height; }
}
