package com.wildlife.model.environment;

import com.wildlife.model.base.Entity;
import com.wildlife.model.animal.Animal;
import com.wildlife.model.plant.Plant;
import com.wildlife.model.enums.Season;
import com.wildlife.observer.EventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Chứa và quản lý tất cả các thực thể trong trò chơi.
 */
public class WorldMap {
    private double width;
    private double height;
    private Season currentSeason;
    
    // Sử dụng CopyOnWriteArrayList để tránh ConcurrentModificationException 
    // khi thêm/xóa thực thể trong lúc duyệt mảng (game loop)
    private List<Entity> entities;
    private List<Zone> zones;
    
    public WorldMap(double width, double height) {
        this.width = width;
        this.height = height;
        this.currentSeason = Season.SPRING;
        this.entities = new CopyOnWriteArrayList<>();
        this.zones = new ArrayList<>();
        
        // Khởi tạo bản đồ mặc định (Tổng hợp)
        loadMapType("Combined");
    }
    
    public void loadMapType(String mapType) {
        zones.clear();
        entities.clear(); // Xóa hết sinh vật cũ khi đổi map
        
        switch (mapType) {
            case "Grassland":
                zones.add(new Grassland(0, 0, width, height));
                break;
            case "Forest":
                zones.add(new Forest(0, 0, width, height));
                break;
            case "Lake":
                zones.add(new Lake(0, 0, width, height)); // Hoặc một cái hồ khổng lồ bao trọn
                break;
            case "Combined":
            default:
                zones.add(new Grassland(0, 0, width, height));
                zones.add(new Forest(width - 300, 0, 300, 250));
                zones.add(new Lake(width / 2 - 150, height - 200, 300, 150));
                break;
        }
        
        EventPublisher.getInstance().publish("MAP_CHANGED");
    }
    
    public void addEntity(Entity entity) {
        entities.add(entity);
        EventPublisher.getInstance().publish("ENTITY_ADDED");
    }
    
    public void removeEntity(Entity entity) {
        entities.remove(entity);
        EventPublisher.getInstance().publish("ENTITY_REMOVED");
    }
    
    /**
     * Cập nhật tất cả các thực thể
     */
    public void updateAll() {
        List<Entity> deadEntities = new ArrayList<>();
        
        for (Entity e : entities) {
            if (e instanceof Animal) {
                ((Animal) e).updateSurvival(this);
            } else if (e instanceof Plant) {
                ((Plant) e).updateSeason(this.currentSeason);
                e.update();
            } else {
                e.update();
            }
            
            if (!e.isAlive()) {
                deadEntities.add(e);
            }
        }
        
        // Xóa các thực thể đã chết khỏi bản đồ
        if (!deadEntities.isEmpty()) {
            entities.removeAll(deadEntities);
            EventPublisher.getInstance().publish("ENTITY_REMOVED");
        }
        
        // Mùa xuân: Hệ sinh thái phục hồi, cỏ tự mọc lại ngẫu nhiên nếu trên bản đồ còn quá ít
        if (currentSeason == Season.SPRING && Math.random() < 0.02) {
            long plantCount = entities.stream().filter(e -> e instanceof Plant).count();
            if (plantCount < 20) {
                // Tự mọc thêm cỏ
                addEntity(new com.wildlife.model.plant.Grass(Math.random() * width, Math.random() * height));
            }
        }
    }
    
    public List<Entity> getEntities() {
        return entities;
    }
    
    public List<Zone> getZones() {
        return zones;
    }
    
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    
    public Season getCurrentSeason() { return currentSeason; }
    public void setSeason(Season season) { 
        this.currentSeason = season; 
        EventPublisher.getInstance().publish("SEASON_CHANGED");
    }
    
    public void nextSeason() {
        Season[] seasons = Season.values();
        int nextOrdinal = (currentSeason.ordinal() + 1) % seasons.length;
        setSeason(seasons[nextOrdinal]);
    }
}
