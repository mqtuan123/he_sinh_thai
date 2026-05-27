package com.wildlife.model.base;

/**
 * Interface dành cho thực thể có thể di chuyển
 */
public interface Movable {
    void move(com.wildlife.model.environment.WorldMap map);
    void setTarget(double targetX, double targetY);
}
