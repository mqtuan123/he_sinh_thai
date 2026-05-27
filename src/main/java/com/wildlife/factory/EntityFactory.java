package com.wildlife.factory;

import com.wildlife.model.base.Entity;

/**
 * Factory Pattern: Interface tạo các thực thể
 */
public interface EntityFactory {
    Entity createEntity(String type, double x, double y);
}
