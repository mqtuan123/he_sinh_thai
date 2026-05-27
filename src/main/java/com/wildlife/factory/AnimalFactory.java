package com.wildlife.factory;

import com.wildlife.model.animal.Rabbit;
import com.wildlife.model.animal.Wolf;
import com.wildlife.model.animal.Deer;
import com.wildlife.model.animal.Tiger;
import com.wildlife.model.animal.Elephant;
import com.wildlife.model.base.Entity;

/**
 * Factory để tạo các loài động vật
 */
public class AnimalFactory implements EntityFactory {
    @Override
    public Entity createEntity(String type, double x, double y) {
        switch (type.toLowerCase()) {
            case "rabbit":
                return new Rabbit(x, y);
            case "wolf":
                return new Wolf(x, y);
            case "deer":
                return new Deer(x, y);
            case "tiger":
                return new Tiger(x, y);
            case "elephant":
                return new Elephant(x, y);
            default:
                throw new IllegalArgumentException("Loài động vật không hợp lệ: " + type);
        }
    }
}
