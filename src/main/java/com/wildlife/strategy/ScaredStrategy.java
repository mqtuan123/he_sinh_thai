package com.wildlife.strategy;

import com.wildlife.model.animal.Animal;
import com.wildlife.model.base.Entity;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.environment.Zone;
import com.wildlife.model.environment.Forest;
import com.wildlife.model.obstacle.Bush;
import com.wildlife.model.enums.AnimalState;

/**
 * Chiến thuật nhút nhát: Ưu tiên bỏ chạy khi thấy kẻ thù, tìm bụi rậm để nấp.
 * Kế thừa logic tìm thức ăn của PassiveStrategy khi an toàn.
 */
public class ScaredStrategy extends PassiveStrategy {

    @Override
    public void executeBehavior(Animal animal, WorldMap map) {
        Entity closestPredator = null;
        double minPredatorDist = animal.getVisionRange();
        
        // Kiểm tra xem có đang đứng trong rừng hoặc bụi rậm không
        boolean isHiding = false;
        for (Entity e : map.getEntities()) {
            if (e instanceof Bush && animal.distanceTo(e) < e.getSize()) {
                isHiding = true;
                break;
            }
        }
        for (Zone z : map.getZones()) {
            if (z instanceof Forest && z.contains(animal.getX(), animal.getY())) {
                isHiding = true;
                break;
            }
        }
        
        // Nếu đang nấp an toàn thì không cần chạy
        if (isHiding) {
            // Vẫn có thể ăn nếu đói, tạm thời reset về IDLE nếu không cần gì
            if (animal.getState() == AnimalState.FLEEING) {
                animal.setState(AnimalState.IDLE);
            }
            // Gọi super.executeBehavior nếu đói, để đi ăn
            if (animal.getHunger() > 60) {
                super.executeBehavior(animal, map);
            }
            return;
        }

        // Tìm kẻ thù (Thú ăn thịt lớn hơn)
        for (Entity e : map.getEntities()) {
            if (e instanceof Animal && e.isAlive() && e != animal) {
                Animal other = (Animal) e;
                // Nếu là kẻ thù (lớn hơn và mang HunterStrategy)
                if (other.getSize() > animal.getSize() && other.getState() != AnimalState.DEAD) {
                    // Để đơn giản, coi tất cả động vật to hơn là mối nguy nếu nó đang tiến lại gần
                    // Hoặc kiểm tra class type cụ thể, hoặc strategy.
                    double dist = animal.distanceTo(other);
                    if (dist < minPredatorDist) {
                        minPredatorDist = dist;
                        closestPredator = other;
                    }
                }
            }
        }

        if (closestPredator != null) {
            // Bỏ chạy
            animal.setState(AnimalState.FLEEING);
            double dx = animal.getX() - closestPredator.getX();
            double dy = animal.getY() - closestPredator.getY();
            
            // Tìm bụi rậm gần nhất để nhắm tới
            Entity closestBush = null;
            double minBushDist = animal.getVisionRange() * 1.5;
            for (Entity e : map.getEntities()) {
                if (e instanceof Bush) {
                    double dist = animal.distanceTo(e);
                    if (dist < minBushDist) {
                        minBushDist = dist;
                        closestBush = e;
                    }
                }
            }
            
            if (closestBush != null) {
                animal.setTarget(closestBush.getX(), closestBush.getY());
            } else {
                // Chạy ngược lại hướng kẻ thù
                animal.setTarget(animal.getX() + dx, animal.getY() + dy);
            }
        } else {
            // An toàn -> Hành xử như bình thường (tìm cỏ, tìm nước)
            super.executeBehavior(animal, map);
        }
    }
}
