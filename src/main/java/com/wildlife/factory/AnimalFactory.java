package com.wildlife.factory;

import com.wildlife.model.animal.*;
import com.wildlife.model.base.Entity;

/**
 * Concrete Factory tạo các loài động vật.
 * Implements EntityFactory → Factory Method Pattern.
 *
 * Tính mở rộng (Extensibility): thêm loài mới (ví dụ Crocodile)
 * chỉ cần: (1) tạo class Crocodile extends Animal,
 *           (2) thêm case "crocodile" ở đây.
 * Không cần sửa Animal, WorldMap hay bất kỳ class nào khác.
 */
public class AnimalFactory implements EntityFactory {
    @Override
    public Entity createEntity(String type, double x, double y) {
        switch (type.toLowerCase()) {
            case "rabbit":   return new Rabbit(x, y);
            case "deer":     return new Deer(x, y);
            case "wolf":     return new Wolf(x, y);
            case "tiger":    return new Tiger(x, y);
            case "elephant": return new Elephant(x, y);
            case "duck":     return new Duck(x, y);
            // Thêm loài mới tại đây ↓
            default: throw new IllegalArgumentException("Loài không hợp lệ: " + type);
        }
    }
}
