package com.wildlife.strategy;

import com.wildlife.app.Config;
import com.wildlife.model.animal.Animal;
import com.wildlife.model.base.Entity;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.environment.Zone;
import com.wildlife.model.plant.Plant;
import com.wildlife.model.enums.AnimalState;
import com.wildlife.sound.SoundManager;

/**
 * AggressiveStrategy — kích hoạt khi đói > 80%.
 * Bất chấp kẻ thù, tầm nhìn tăng gấp đôi, ưu tiên ăn > uống.
 * Ngưỡng nhất quán với PassiveStrategy/ScaredStrategy (60%).
 */
public class AggressiveStrategy implements SurvivalStrategy {


    @Override
    public void executeBehavior(Animal animal, WorldMap map) {

        // Uống nước nếu khát > 60% — nhất quán với các strategy khác
        if (animal.getThirst() > Config.SURVIVAL_URGENT) {
            Zone lake = animal.findNearestLake(map);
            if (lake != null) {
                if (lake.contains(animal.getX(), animal.getY())) {
                    animal.drink(50);
                    SoundManager.getInstance().playDrink();
                    animal.setState(AnimalState.DRINKING);
                } else {
                    animal.setTarget(lake.getX() + lake.getWidth()  / 2,
                                     lake.getY() + lake.getHeight() / 2);
                    animal.setState(AnimalState.WANDERING);
                }
                return;
            }
        }

        // Tìm Plant gần nhất — tầm nhìn x2, bỏ qua mọi nguy hiểm
        Entity bestFood   = null;
        double minDist    = animal.getVisionRange() * 2;

        for (Entity e : map.getEntities()) {
            if (!(e instanceof Plant) || !e.isAlive()) continue;
            Plant p = (Plant) e;
            if (!p.canBeEaten()) continue;
            double dist = animal.distanceTo(e);
            if (dist < minDist) { minDist = dist; bestFood = e; }
        }

        if (bestFood != null) {
            animal.setState(AnimalState.HUNTING);
            animal.setTarget(bestFood.getX(), bestFood.getY());

            double eatRange = animal.getSize() / 2 + bestFood.getSize() / 2 + 5;
            if (minDist < eatRange) {
                double nutrition = ((Plant) bestFood).beEaten(20);
                animal.eat(nutrition);
                SoundManager.getInstance().playEat();
                animal.setState(AnimalState.EATING);
            }
        } else {
            // Không thấy thức ăn → quét rộng hơn
            double rx = Math.max(0, Math.min(animal.getX() + (Math.random() * 300 - 150), map.getWidth()));
            double ry = Math.max(0, Math.min(animal.getY() + (Math.random() * 300 - 150), map.getHeight()));
            animal.setTarget(rx, ry);
            animal.setState(AnimalState.WANDERING);
        }
    }
}
