package com.wildlife.factory;

import com.wildlife.model.base.Entity;
import com.wildlife.model.plant.FruitTree;
import com.wildlife.model.plant.Grass;

/**
 * Factory để tạo các loài thực vật
 */
public class PlantFactory implements EntityFactory {
    @Override
    public Entity createEntity(String type, double x, double y) {
        switch (type.toLowerCase()) {
            case "grass":
                return new Grass(x, y);
            case "tree":
                return new FruitTree(x, y);
            default:
                throw new IllegalArgumentException("Loài thực vật không hợp lệ: " + type);
        }
    }
}
