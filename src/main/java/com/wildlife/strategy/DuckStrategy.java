package com.wildlife.strategy;

import com.wildlife.model.animal.Animal;
import com.wildlife.model.environment.Lake;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.environment.Zone;
import com.wildlife.model.enums.AnimalState;
import com.wildlife.sound.SoundManager;

/**
 * DuckStrategy — kế thừa PassiveStrategy, override seekFood().
 *
 * Vịt ăn bằng cách bơi trong hồ (ăn + uống cùng lúc):
 *   - Nếu đang ở trong Lake → tự động ăn + uống mỗi frame (giảm cả đói lẫn khát)
 *   - Nếu chưa ở Lake → bơi vào Lake gần nhất
 *   - Fallback: nếu không có Lake → ăn Plant như PassiveStrategy
 *
 * Vịt KHÔNG bị giảm tốc trong Lake (override getSpeedModifier ở Duck constructor).
 */
public class DuckStrategy extends PassiveStrategy {

    private static final double LAKE_FEED_NUTRITION = 15.0; // ăn mỗi frame trong hồ
    private static final double LAKE_FEED_WATER     = 10.0; // uống mỗi frame trong hồ

    @Override
    public void executeBehavior(Animal animal, WorldMap map) {

        // Kiểm tra đang ở trong Lake không
        Zone currentLake = null;
        for (Zone z : map.getZones()) {
            if (z instanceof Lake && z.contains(animal.getX(), animal.getY())) {
                currentLake = z;
                break;
            }
        }

        if (currentLake != null) {
            // Đang trong hồ → ăn + uống tự nhiên mỗi frame
            if (animal.getHunger() > 5)  {
                animal.eat(LAKE_FEED_NUTRITION);
                SoundManager.getInstance().playEat();
            }
            if (animal.getThirst() > 5) {
                animal.drink(LAKE_FEED_WATER);
            }
            // Vẫn di chuyển nhẹ trong hồ (không đứng im hoàn toàn)
            if (animal.getState() == AnimalState.IDLE || animal.getState() == AnimalState.EATING) {
                double rx = clamp(animal.getX() + (Math.random() * 60 - 30), 0, map.getWidth());
                double ry = clamp(animal.getY() + (Math.random() * 60 - 30), 0, map.getHeight());
                animal.setTarget(rx, ry);
                animal.setState(AnimalState.WANDERING);
            }
            return;
        }

        // Đang ở ngoài hồ
        double hunger = animal.getHunger();
        double thirst = animal.getThirst();

        // Đói > 30% hoặc khát > 30% → bơi vào hồ
        if (hunger > 30 || thirst > 30) {
            Zone nearestLake = animal.findNearestLake(map);
            if (nearestLake != null) {
                animal.setTarget(
                    nearestLake.getX() + nearestLake.getWidth()  / 2,
                    nearestLake.getY() + nearestLake.getHeight() / 2
                );
                animal.setState(AnimalState.WANDERING);
                return;
            }
        }

        // Không có hồ → fallback ăn Plant như PassiveStrategy
        super.executeBehavior(animal, map);
    }
}
