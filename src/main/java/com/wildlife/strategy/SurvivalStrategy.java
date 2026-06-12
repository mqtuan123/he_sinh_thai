package com.wildlife.strategy;

import com.wildlife.model.animal.Animal;
import com.wildlife.model.environment.WorldMap;

/**
 * Strategy Pattern — Interface định nghĩa "bộ não" sinh tồn.
 * Mỗi loài có một SurvivalStrategy riêng, có thể thay đổi tại runtime.
 *
 * Ví dụ: Thỏ dùng ScaredStrategy → khi đói >80% đổi sang AggressiveStrategy
 *        → khi no trở lại ScaredStrategy.
 *
 * Áp dụng Open/Closed: thêm chiến thuật mới (SneakyStrategy) không cần
 * sửa Animal hay các strategy hiện có.
 */
public interface SurvivalStrategy {
    void executeBehavior(Animal animal, WorldMap map);
}
