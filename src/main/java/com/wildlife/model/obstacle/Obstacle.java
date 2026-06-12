package com.wildlife.model.obstacle;
import com.wildlife.model.base.Entity;
public abstract class Obstacle extends Entity {
    public Obstacle(double x, double y, double size) { super(x, y, size); }
    @Override public void update() {}
}
