package com.wildlife.factory;

import com.wildlife.model.base.Entity;

/**
 * Factory Pattern — Interface chung cho mọi loại factory.
 * Tuân theo Open/Closed Principle: thêm loài mới chỉ cần thêm
 * subclass Factory + case trong switch, không sửa code hiện có.
 *
 * Áp dụng:
 *  - AnimalFactory  → tạo các loài động vật
 *  - PlantFactory   → tạo các loài thực vật
 *  - ObstacleFactory→ tạo vật cản
 */
public interface EntityFactory {
    Entity createEntity(String type, double x, double y);
}
