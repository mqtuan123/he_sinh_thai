package com.wildlife.factory;

import com.wildlife.model.base.Entity;
import com.wildlife.model.obstacle.Rock;
import com.wildlife.model.obstacle.Bush;

/**
 * Factory để tạo các vật cản
 */
public class ObstacleFactory implements EntityFactory {
    @Override
    public Entity createEntity(String type, double x, double y) {
        switch (type.toLowerCase()) {
            case "rock":
                return new Rock(x, y);
            case "bush":
                return new Bush(x, y);
            default:
                throw new IllegalArgumentException("Vật cản không hợp lệ: " + type);
        }
    }
}
