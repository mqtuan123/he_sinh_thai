package com.wildlife.model.environment;

import com.wildlife.model.base.Entity;
import com.wildlife.model.animal.Animal;
import com.wildlife.model.plant.Plant;
import com.wildlife.model.plant.FruitTree;
import com.wildlife.model.plant.Grass;
import com.wildlife.model.enums.Season;
import com.wildlife.observer.EventPublisher;
import com.wildlife.sound.SoundManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Chứa và quản lý tất cả các thực thể trong trò chơi.
 * Fix: dùng isReadyToRemove() thay vì !isAlive() để cho phép hiệu ứng xác mờ dần.
 */
public class WorldMap {
    private double width;
    private double height;
    private Season currentSeason;

    private List<Entity> entities;
    private List<Zone>   zones;

    public WorldMap(double width, double height) {
        this.width         = width;
        this.height        = height;
        this.currentSeason = Season.SPRING;
        this.entities      = new CopyOnWriteArrayList<>();
        this.zones         = new ArrayList<>();
        loadMapType("Combined");
    }

    public void loadMapType(String mapType) {
        zones.clear();
        entities.clear();

        switch (mapType) {
            case "Grassland":
                zones.add(new Grassland(0, 0, width, height));
                break;
            case "Forest":
                zones.add(new Forest(0, 0, width, height));
                break;
            case "Lake":
                zones.add(new Lake(0, 0, width, height));
                break;
            case "Combined":
            default:
                zones.add(new Grassland(0, 0, width, height));
                zones.add(new Forest(width - 300, 0, 300, 250));
                zones.add(new Lake(width / 2 - 150, height - 200, 300, 150));
                zones.add(new Mud(50, height - 160, 180, 100));
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

    public void updateAll() {
        List<Entity> toRemove = new ArrayList<>();

        for (Entity e : entities) {
            if (e instanceof Animal) {
                Animal a = (Animal) e;
                a.updateSurvival(this);
                // Xoá khi xác đã hết thời gian linger (sau hiệu ứng mờ dần)
                if (a.isReadyToRemove()) toRemove.add(e);
            } else if (e instanceof Plant) {
                ((Plant) e).updateSeason(this.currentSeason);
                e.update();
                if (!e.isAlive()) toRemove.add(e);
            } else {
                e.update();
                if (!e.isAlive()) toRemove.add(e);
            }
        }

        if (!toRemove.isEmpty()) {
            entities.removeAll(toRemove);
            EventPublisher.getInstance().publish("ENTITY_REMOVED");
        }

        // Mùa Xuân: tự mọc thêm thực vật nếu quá ít
        if (currentSeason == Season.SPRING && Math.random() < 0.025) {
            long plantCount = entities.stream()
                .filter(e -> e instanceof Plant)
                .count();
            if (plantCount < 25) {
                boolean growTree = Math.random() < 0.25;
                double rx = Math.random() * width;
                double ry = Math.random() * height;
                addEntity(growTree
                    ? new FruitTree(rx, ry)
                    : new Grass(rx, ry));
            }
        }
    }

    public List<Entity> getEntities()  { return entities; }
    public List<Zone>   getZones()     { return zones; }
    public double getWidth()           { return width; }
    public double getHeight()          { return height; }
    public Season getCurrentSeason()   { return currentSeason; }

    public void setSeason(Season season) {
        this.currentSeason = season;
        EventPublisher.getInstance().publish("SEASON_CHANGED");
        SoundManager.getInstance().playSeasonChange();
    }

    public void nextSeason() {
        Season[] seasons     = Season.values();
        int      nextOrdinal = (currentSeason.ordinal() + 1) % seasons.length;
        setSeason(seasons[nextOrdinal]);
    }
}
