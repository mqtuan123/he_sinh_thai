package com.wildlife.strategy;

import com.wildlife.model.animal.Animal;
import com.wildlife.model.base.Entity;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.environment.Zone;
import com.wildlife.model.environment.Lake;
import com.wildlife.model.plant.Plant;
import com.wildlife.model.enums.AnimalState;

/**
 * Chiến thuật tìm cỏ/cây để ăn, hoặc chạy rông (dành cho động vật ăn cỏ)
 */
public class PassiveStrategy implements SurvivalStrategy {

    @Override
    public void executeBehavior(Animal animal, WorldMap map) {
        // Ưu tiên cao nhất: Khát nước
        if (animal.getThirst() > 70) {
            Zone nearestLake = null;
            double minLakeDist = Double.MAX_VALUE;
            
            for (Zone z : map.getZones()) {
                if (z instanceof Lake) {
                    // Dùng tâm của hồ để tính
                    double lakeCenterX = z.getX() + z.getWidth() / 2;
                    double lakeCenterY = z.getY() + z.getHeight() / 2;
                    double dx = animal.getX() - lakeCenterX;
                    double dy = animal.getY() - lakeCenterY;
                    double dist = Math.sqrt(dx*dx + dy*dy);
                    
                    if (dist < minLakeDist) {
                        minLakeDist = dist;
                        nearestLake = z;
                    }
                }
            }
            
            if (nearestLake != null) {
                // Nếu đã đứng trong hồ thì uống nước
                if (nearestLake.contains(animal.getX(), animal.getY())) {
                    animal.drink(50);
                    animal.setState(AnimalState.IDLE);
                } else {
                    // Đi tới hồ
                    animal.setState(AnimalState.WANDERING);
                    animal.setTarget(nearestLake.getX() + nearestLake.getWidth()/2, 
                                     nearestLake.getY() + nearestLake.getHeight()/2);
                }
                return; // Đang tìm nước thì bỏ qua tìm thức ăn
            }
        }
        
        // Nếu đang no, chỉ đi lang thang
        if (animal.getHunger() < 50) {
            if (animal.getState() == AnimalState.IDLE) {
                // Chọn một điểm ngẫu nhiên để đi
                double rx = animal.getX() + (Math.random() * 100 - 50);
                double ry = animal.getY() + (Math.random() * 100 - 50);
                
                // Ràng buộc trong bản đồ
                rx = Math.max(0, Math.min(rx, map.getWidth()));
                ry = Math.max(0, Math.min(ry, map.getHeight()));
                
                animal.setTarget(rx, ry);
                animal.setState(AnimalState.WANDERING);
            }
            return;
        }

        // Nếu đói, tìm thức ăn (Plant) gần nhất
        Entity closestFood = null;
        double minDistance = animal.getVisionRange();

        for (Entity e : map.getEntities()) {
            if (e instanceof Plant && e.isAlive()) {
                double dist = animal.distanceTo(e);
                if (dist < minDistance) {
                    minDistance = dist;
                    closestFood = e;
                }
            }
        }

        if (closestFood != null) {
            animal.setState(AnimalState.HUNTING); // Dùng chung state đi tới mục tiêu
            animal.setTarget(closestFood.getX(), closestFood.getY());
            
            // Nếu đã đến đủ gần để ăn
            if (minDistance < animal.getSize() / 2 + closestFood.getSize() / 2 + 5) {
                Plant food = (Plant) closestFood;
                double nutrition = food.beEaten(20); // Ăn một miếng
                animal.eat(nutrition);
                animal.setState(AnimalState.IDLE); // Reset state sau khi ăn để không bị kẹt
            }
        } else {
            // Không thấy thức ăn, đi lang thang tìm tiếp
            if (animal.getState() != AnimalState.WANDERING) {
                animal.setState(AnimalState.IDLE); // Reset để frame sau chọn điểm mới
            }
        }
    }
}
