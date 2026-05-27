package com.wildlife.strategy;

import com.wildlife.model.animal.Animal;
import com.wildlife.model.environment.WorldMap;

/**
 * Strategy Pattern: Định nghĩa giao diện chung cho các chiến thuật sinh tồn.
 */
public interface SurvivalStrategy {
    /**
     * Thực thi hành vi của con vật dựa vào trạng thái hiện tại và môi trường.
     * @param animal Con vật đang thực thi chiến thuật
     * @param map Bản đồ thế giới chứa các thực thể khác để tương tác
     */
    void executeBehavior(Animal animal, WorldMap map);
}
