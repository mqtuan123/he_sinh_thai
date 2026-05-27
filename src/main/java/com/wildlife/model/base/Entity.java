package com.wildlife.model.base;

/**
 * Abstract class gốc cho mọi thực thể trong game (Động vật, Thực vật, Vật cản).
 */
public abstract class Entity implements Renderable {
    protected double x;
    protected double y;
    protected double size;
    protected String id;
    protected boolean isAlive = true;

    public Entity(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.id = java.util.UUID.randomUUID().toString();
    }

    /**
     * Cập nhật logic của thực thể theo mỗi frame (Vòng đời).
     */
    public abstract void update();

    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return size; }
    public String getId() { return id; }
    public boolean isAlive() { return isAlive; }
    
    public void setAlive(boolean alive) { this.isAlive = alive; }
    
    /**
     * Tính khoảng cách tới một entity khác
     */
    public double distanceTo(Entity other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
}
