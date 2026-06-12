package com.wildlife.factory;

import com.wildlife.model.base.Entity;
import com.wildlife.model.plant.*;

/**
 * Concrete Factory tạo thực vật.
 * Thêm loài thực vật mới: tạo class extends Plant, thêm case ở đây.
 */
public class PlantFactory implements EntityFactory {
    @Override
    public Entity createEntity(String type, double x, double y) {
        switch (type.toLowerCase()) {
            case "grass": return new Grass(x, y);
            case "tree":  return new FruitTree(x, y);
            default: throw new IllegalArgumentException("Thực vật không hợp lệ: " + type);
        }
    }
}
