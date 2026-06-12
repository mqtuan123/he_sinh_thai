package com.wildlife.strategy;

import com.wildlife.model.animal.Animal;
import com.wildlife.model.base.Entity;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.environment.Zone;
import com.wildlife.model.environment.Lake;
import com.wildlife.model.enums.AnimalState;

/**
 * Strategy Pattern: Chiến thuật "hung hãn" - khi cực kỳ đói (hunger > 80),
 * động vật ăn cỏ trở nên liều lĩnh, lao thẳng tới nguồn nước/thức ăn
 * gần nhất bất kể nguy hiểm, bỏ qua việc lẩn trốn.
 *
 * Tự kích hoạt khi hunger > 80, tự reset (trả lại strategy gốc) khi hunger < 40
 * để tránh dao động (oscillation) liên tục giữa 2 strategy.
 */
public class AggressiveStrategy implements SurvivalStrategy {

    // Strategy "bình thường" để quay lại khi đã no
    private SurvivalStrategy fallbackStrategy;

    public AggressiveStrategy(SurvivalStrategy fallbackStrategy) {
        this.fallbackStrategy = fallbackStrategy;
    }

    @Override
    public void executeBehavior(Animal animal, WorldMap map) {
        // Nếu đã hồi phục (hunger < 40) -> quay về strategy gốc
        if (animal.getHunger() < 40) {
            animal.setStrategy(fallbackStrategy);
            if (fallbackStrategy != null) {
                fallbackStrategy.executeBehavior(animal, map);
            }
            return;
        }

        // Ưu tiên nước nếu quá khát
        if (animal.getThirst() > 70) {
            Zone nearestLake = null;
            double minLakeDist = Double.MAX_VALUE;

            for (Zone z : map.getZones()) {
                if (z instanceof Lake) {
                    double lakeCenterX = z.getX() + z.getWidth() / 2;
                    double lakeCenterY = z.getY() + z.getHeight() / 2;
                    double dx = animal.getX() - lakeCenterX;
                    double dy = animal.getY() - lakeCenterY;
                    double dist = Math.sqrt(dx * dx + dy * dy);

                    if (dist < minLakeDist) {
                        minLakeDist = dist;
                        nearestLake = z;
                    }
                }
            }

            if (nearestLake != null) {
                if (nearestLake.contains(animal.getX(), animal.getY())) {
                    animal.drink(50);
                    animal.setState(AnimalState.IDLE);
                } else {
                    animal.setState(AnimalState.HUNTING); // dùng tốc độ cao
                    animal.setTarget(nearestLake.getX() + nearestLake.getWidth() / 2,
                                     nearestLake.getY() + nearestLake.getHeight() / 2);
                }
                return;
            }
        }

        // Liều lĩnh tìm bất kỳ thức ăn (Plant) gần nhất, không quan tâm ẩn nấp/nguy hiểm
        Entity closestFood = null;
        double minDistance = animal.getVisionRange() * 1.5; // tầm nhìn mở rộng vì quá đói

        for (Entity e : map.getEntities()) {
            if (e instanceof com.wildlife.model.plant.Plant && e.isAlive()) {
                double dist = animal.distanceTo(e);
                if (dist < minDistance) {
                    minDistance = dist;
                    closestFood = e;
                }
            }
        }

        if (closestFood != null) {
            animal.setState(AnimalState.HUNTING);
            animal.setTarget(closestFood.getX(), closestFood.getY());

            if (minDistance < animal.getSize() / 2 + closestFood.getSize() / 2 + 5) {
                com.wildlife.model.plant.Plant food = (com.wildlife.model.plant.Plant) closestFood;
                double nutrition = food.beEaten(30); // ăn liều, miếng to hơn bình thường
                animal.eat(nutrition);
                animal.setState(AnimalState.IDLE);
            }
        } else {
            // Không có thức ăn -> đi lang thang liều lĩnh tìm kiếm
            if (animal.getState() != AnimalState.WANDERING) {
                double rx = animal.getX() + (Math.random() * 200 - 100);
                double ry = animal.getY() + (Math.random() * 200 - 100);
                rx = Math.max(0, Math.min(rx, map.getWidth()));
                ry = Math.max(0, Math.min(ry, map.getHeight()));
                animal.setTarget(rx, ry);
                animal.setState(AnimalState.WANDERING);
            }
        }
    }
}
