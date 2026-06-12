package com.wildlife.strategy;

import com.wildlife.model.animal.Animal;
import com.wildlife.model.base.Entity;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.plant.FruitTree;

/**
 * ElephantStrategy — kế thừa PassiveStrategy, override scoreFood().
 * Voi ưu tiên FruitTree (bonus -50) trước Grass (khoảng cách thuần).
 * Không sợ kẻ thù — không override phần flee.
 */
public class ElephantStrategy extends PassiveStrategy {

    @Override
    protected double scoreFood(Animal animal, Entity food, double dist) {
        // FruitTree được ưu tiên hơn: trừ 50 điểm "ảo" → chọn cây trước
        // dù cách xa hơn chút (< 50 đơn vị)
        if (food instanceof FruitTree) return dist - 50;
        return dist;
    }
}
