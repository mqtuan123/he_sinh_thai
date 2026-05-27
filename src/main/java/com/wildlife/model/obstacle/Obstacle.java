package com.wildlife.model.obstacle;

import com.wildlife.model.base.Entity;

/**
 * Lớp trừu tượng cho mọi vật cản (Đá, Cây lớn...)
 */
public abstract class Obstacle extends Entity {
    public Obstacle(double x, double y, double size) {
        super(x, y, size);
    }

    @Override
    public void update() {
        // Vật cản mặc định đứng yên, không có logic cập nhật
    }
}
