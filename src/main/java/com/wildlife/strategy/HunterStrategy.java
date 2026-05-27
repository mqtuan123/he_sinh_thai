package com.wildlife.strategy;

import com.wildlife.model.animal.Animal;
import com.wildlife.model.base.Entity;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.environment.Zone;
import com.wildlife.model.environment.Lake;
import com.wildlife.model.environment.Forest;
import com.wildlife.model.obstacle.Bush;
import com.wildlife.model.enums.AnimalState;

/**
 * Chiến thuật săn mồi (dành cho động vật ăn thịt)
 */
public class HunterStrategy implements SurvivalStrategy {

    @Override
    public void executeBehavior(Animal animal, WorldMap map) {
        // Ưu tiên cao nhất: Khát nước
        if (animal.getThirst() > 70) {
            Zone nearestLake = null;
            double minLakeDist = Double.MAX_VALUE;
            
            for (Zone z : map.getZones()) {
                if (z instanceof Lake) {
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
                if (nearestLake.contains(animal.getX(), animal.getY())) {
                    animal.drink(50);
                    animal.setState(AnimalState.IDLE);
                } else {
                    animal.setState(AnimalState.WANDERING);
                    animal.setTarget(nearestLake.getX() + nearestLake.getWidth()/2, 
                                     nearestLake.getY() + nearestLake.getHeight()/2);
                }
                return; // Đang tìm nước thì bỏ qua săn mồi
            }
        }
        
        if (animal.getHunger() < 40) {
            // Đã no, đi lang thang
            if (animal.getState() == AnimalState.IDLE) {
                double rx = animal.getX() + (Math.random() * 100 - 50);
                double ry = animal.getY() + (Math.random() * 100 - 50);
                
                rx = Math.max(0, Math.min(rx, map.getWidth()));
                ry = Math.max(0, Math.min(ry, map.getHeight()));
                
                animal.setTarget(rx, ry);
                animal.setState(AnimalState.WANDERING);
            }
            return;
        }

        // Đói -> Tìm con mồi (những Animal có kích thước nhỏ hơn hoặc thuộc loại bị săn)
        Entity closestPrey = null;
        double minDistance = animal.getVisionRange();

        for (Entity e : map.getEntities()) {
            if (e instanceof Animal && e != animal && e.isAlive()) {
                Animal other = (Animal) e;
                // Luật săn mồi đơn giản: Săn con nhỏ hơn và đang không phải là thú ăn thịt cùng loài
                if (other.getSize() < animal.getSize() && !other.getClass().equals(animal.getClass())) {
                    
                    // Kiểm tra xem con mồi có đang nấp không (tàng hình)
                    boolean isPreyHiding = false;
                    for (Entity obstacle : map.getEntities()) {
                        if (obstacle instanceof Bush && other.distanceTo(obstacle) < obstacle.getSize()) {
                            isPreyHiding = true;
                            break;
                        }
                    }
                    if (!isPreyHiding) {
                        for (Zone z : map.getZones()) {
                            if (z instanceof Forest && z.contains(other.getX(), other.getY())) {
                                isPreyHiding = true;
                                break;
                            }
                        }
                    }
                    
                    if (isPreyHiding) continue; // Bỏ qua con mồi này vì nó đang tàng hình
                    
                    double dist = animal.distanceTo(other);
                    if (dist < minDistance) {
                        minDistance = dist;
                        closestPrey = other;
                    }
                }
            }
        }

        if (closestPrey != null) {
            animal.setState(AnimalState.HUNTING);
            animal.setTarget(closestPrey.getX(), closestPrey.getY());
            
            // Nếu đủ gần để vồ
            if (minDistance < animal.getSize() / 2 + closestPrey.getSize() / 2 + 5) {
                animal.eat(50); // Ăn thịt được 50 no
                closestPrey.setAlive(false); // Con mồi chết
                animal.setState(AnimalState.IDLE);
            }
        } else {
            // Không thấy con mồi thì đi lang thang
            if (animal.getState() != AnimalState.WANDERING) {
                animal.setState(AnimalState.IDLE);
            }
        }
    }
}
