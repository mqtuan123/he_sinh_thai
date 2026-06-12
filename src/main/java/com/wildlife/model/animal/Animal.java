package com.wildlife.model.animal;

import com.wildlife.app.Config;
import com.wildlife.model.base.Entity;
import com.wildlife.model.base.Movable;
import com.wildlife.model.enums.AnimalState;
import com.wildlife.model.enums.Season;
import com.wildlife.model.environment.Forest;
import com.wildlife.model.environment.Zone;
import com.wildlife.strategy.AggressiveStrategy;
import com.wildlife.strategy.HunterStrategy;
import com.wildlife.strategy.SurvivalStrategy;

/**
 * Lớp trừu tượng cho tất cả các loại động vật.
 */
public abstract class Animal extends Entity implements Movable {
    protected double health = Config.MAX_HEALTH;
    protected double hunger = 0; // 0 là no, 100 là chết đói
    protected double thirst = 0; // 0 là không khát, 100 là chết khát
    protected double energy = 100;
    
    protected double baseSpeed;
    protected double speed;
    protected double visionRange = Config.VISION_RANGE_DEFAULT;
    
    protected AnimalState state = AnimalState.IDLE;
    protected SurvivalStrategy strategy;
    protected SurvivalStrategy originalStrategy; // Lưu strategy gốc để AggressiveStrategy quay về
    
    // Target cho di chuyển
    protected double targetX;
    protected double targetY;
    
    // Đối tượng đang bị nhắm tới (để ăn hoặc chạy trốn)
    protected Entity targetEntity;

    public Animal(double x, double y, double size, double speed) {
        super(x, y, size);
        this.baseSpeed = speed;
        this.speed = speed;
        this.targetX = x;
        this.targetY = y;
    }
    
    public void setStrategy(SurvivalStrategy strategy) {
        this.strategy = strategy;
        if (this.originalStrategy == null) {
            this.originalStrategy = strategy;
        }
    }

    @Override
    public void update() {
        if (!isAlive) return;

        // Giảm chỉ số sinh tồn theo thời gian
        hunger += Config.HUNGER_RATE;
        thirst += Config.THIRST_RATE;
        
        if (hunger >= Config.MAX_HUNGER || thirst >= Config.MAX_THIRST || health <= 0) {
            die();
            return;
        }
        
        // Nếu có chiến thuật, thực thi nó (Bản đồ sẽ được truyền vào từ GameLoop, ở đây tạm thời gọi qua map nếu cần, nhưng chuẩn nhất là GameLoop gọi map update -> map gọi entity update. Ta sẽ sửa hàm update có tham số WorldMap, hoặc tách riêng logic behavior)
    }
    
    // Chỉnh sửa lại hàm update để nhận WorldMap
    public void updateSurvival(com.wildlife.model.environment.WorldMap map) {
        if (!isAlive) return;
        
        // Cập nhật tốc độ theo trạng thái (tăng tốc khi săn mồi hoặc chạy trốn)
        if (state == AnimalState.HUNTING || state == AnimalState.FLEEING) {
            speed = baseSpeed * 1.5;
        } else {
            speed = baseSpeed;
        }

        // Áp dụng hệ số tốc độ theo địa hình hiện tại (Grassland 1.0, Forest 0.7, Lake 0.5)
        for (Zone z : map.getZones()) {
            if (z.contains(x, y)) {
                speed *= z.getSpeedModifier();
                break; // chỉ áp dụng zone đầu tiên chứa vị trí
            }
        }
        
        // Cập nhật chỉ số
        hunger += Config.HUNGER_RATE;
        thirst += Config.THIRST_RATE;

        Season season = map.getCurrentSeason();

        // Mùa hè: khát nhanh hơn
        if (season == Season.SUMMER) {
            thirst += Config.THIRST_RATE * 0.5; // +50% tốc độ khát
        }

        // Mùa đông: mất máu nếu không ở trong Forest (rừng giúp tránh lạnh)
        if (season == Season.WINTER) {
            boolean inForest = false;
            for (Zone z : map.getZones()) {
                if (z instanceof Forest && z.contains(x, y)) {
                    inForest = true;
                    break;
                }
            }
            if (!inForest) {
                health -= 0.05; // mất máu chậm vì lạnh
            }
        }

        if (hunger >= Config.MAX_HUNGER || thirst >= Config.MAX_THIRST || health <= 0) {
            die();
            return;
        }

        // Auto-switch sang AggressiveStrategy khi quá đói (hunger > 80)
        // Chỉ áp dụng cho thú ăn cỏ (không override HunterStrategy của thú săn mồi)
        if (hunger > 80 && !(strategy instanceof AggressiveStrategy)
                && !(strategy instanceof HunterStrategy)) {
            strategy = new AggressiveStrategy(originalStrategy);
        }

        if (strategy != null) {
            strategy.executeBehavior(this, map);
        }
        
        // Sinh sản vào mùa xuân
        if (map.getCurrentSeason() == Season.SPRING) {
            // Tỷ lệ sinh sản: No và Khát thấp, ngẫu nhiên 0.05% mỗi frame (rất nhỏ để tránh quá tải)
            if (hunger < 30 && thirst < 30 && Math.random() < 0.0005) {
                reproduce(map);
            }
        }
        
        // Luôn di chuyển nếu đang không rảnh rỗi hoặc ngủ
        if (state != AnimalState.IDLE && state != AnimalState.SLEEPING && state != AnimalState.EATING && state != AnimalState.DRINKING) {
            move(map);
        }
    }

    @Override
    public void move(com.wildlife.model.environment.WorldMap map) {
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        double moveX = 0;
        double moveY = 0;

        if (distance > speed) {
            moveX = (dx / distance) * speed;
            moveY = (dy / distance) * speed;
        } else {
            x = targetX;
            y = targetY;
            if (state == AnimalState.WANDERING) {
                state = AnimalState.IDLE;
            }
            return;
        }

        // Boids/Steering behavior: Né vật cản (Obstacle Avoidance)
        double repulsionX = 0;
        double repulsionY = 0;
        
        for (Entity e : map.getEntities()) {
            if (e != this && (e instanceof com.wildlife.model.obstacle.Obstacle || 
                             (e instanceof Animal && e.getSize() > this.getSize()))) {
                double dist = this.distanceTo(e);
                double safeDistance = this.getSize() / 2 + e.getSize() / 2 + 10; // Khoảng cách an toàn
                
                if (dist < safeDistance && dist > 0) {
                    // Tạo lực đẩy ngược lại với vật cản
                    double force = (safeDistance - dist) / safeDistance;
                    repulsionX += ((this.x - e.getX()) / dist) * force * speed * 2;
                    repulsionY += ((this.y - e.getY()) / dist) * force * speed * 2;
                }
            }
        }
        
        // Áp dụng di chuyển + lực đẩy
        x += moveX + repulsionX;
        y += moveY + repulsionY;
        
        // Giới hạn trong bản đồ
        x = Math.max(0, Math.min(x, map.getWidth()));
        y = Math.max(0, Math.min(y, map.getHeight()));
    }

    @Override
    public void setTarget(double targetX, double targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void eat(double nutrition) {
        hunger -= nutrition;
        if (hunger < 0) hunger = 0;
        state = AnimalState.EATING;
    }

    public void drink(double waterAmount) {
        thirst -= waterAmount;
        if (thirst < 0) thirst = 0;
        state = AnimalState.DRINKING;
    }
    
    public void takeDamage(double damage) {
        health -= damage;
        if (health <= 0) {
            die();
        }
    }

    protected void die() {
        this.isAlive = false;
        this.state = AnimalState.DEAD;
    }
    
    // Sinh sản con non
    protected void reproduce(com.wildlife.model.environment.WorldMap map) {
        try {
            // Khởi tạo một đối tượng mới cùng class
            Animal baby = this.getClass().getDeclaredConstructor(double.class, double.class)
                              .newInstance(x + 20, y + 20);
            map.addEntity(baby);
            this.hunger += 20; // Đẻ xong sẽ đói hơn
        } catch (Exception e) {
            // Bỏ qua nếu có lỗi reflection
        }
    }

    // Getters and Setters
    public AnimalState getState() { return state; }
    public void setState(AnimalState state) { this.state = state; }
    
    public double getHealth() { return health; }
    public double getHunger() { return hunger; }
    public double getThirst() { return thirst; }
    public double getVisionRange() { return visionRange; }
    public double getSpeed() { return speed; }
    public Entity getTargetEntity() { return targetEntity; }
    public void setTargetEntity(Entity entity) { this.targetEntity = entity; }
}
