package com.wildlife.model.base;

import com.wildlife.model.environment.WorldMap;

/**
 * Interface Movable — mọi thực thể có thể di chuyển.
 * Cho phép WorldMap gọi move() mà không cần biết loài cụ thể.
 * Áp dụng Dependency Inversion Principle (DIP).
 */
public interface Movable {
    void move(WorldMap map);
    void setTarget(double targetX, double targetY);
}
