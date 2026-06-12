package com.wildlife.model.animal;

import com.wildlife.app.Config;
import com.wildlife.model.base.Entity;
import com.wildlife.model.base.Movable;
import com.wildlife.model.enums.AnimalState;
import com.wildlife.model.enums.Season;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.environment.Zone;
import com.wildlife.model.environment.Forest;
import com.wildlife.model.environment.Lake;
import com.wildlife.model.obstacle.Obstacle;
import com.wildlife.strategy.AggressiveStrategy;
import com.wildlife.strategy.HunterStrategy;
import com.wildlife.strategy.SurvivalStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Lớp trừu tượng cho mọi động vật.
 *
 * ┌─ Fix ────────────────────────────────────────────────────────────────────┐
 * │ • createOffspring() abstract — không còn reflection                       │
 * │ • clampPosition() đúng với size/2                                         │
 * │ • Aggressive threshold dùng Config constants                               │
 * │ • findNearestLake() helper — xoá trùng lặp ở mọi Strategy                │
 * │ • beEaten guard → thirst seek ngưỡng từ Config                            │
 * └──────────────────────────────────────────────────────────────────────────┘
 * ┌─ Thêm ───────────────────────────────────────────────────────────────────┐
 * │ • renderStatusBars() — 3 thanh HP/Hunger/Thirst                           │
 * │ • getDeathAlpha() — hiệu ứng xác mờ dần 2 giây                           │
 * │ • getInfo() — chuỗi popup khi click                                       │
 * │ • renderStateLabel() — nhãn trạng thái nhỏ khi FLEEING/HUNTING            │
 * └──────────────────────────────────────────────────────────────────────────┘
 */
public abstract class Animal extends Entity implements Movable {

    protected double health;
    protected double maxHealth;
    protected double hunger = 0;
    protected double thirst = 0;

    protected double baseSpeed;
    protected double speed;
    protected double visionRange = Config.VISION_RANGE_DEFAULT;

    protected AnimalState      state           = AnimalState.IDLE;
    protected SurvivalStrategy strategy;
    protected SurvivalStrategy defaultStrategy;

    private boolean isAggressive = false;

    private int     deathTimer = 0;

    protected double targetX;
    protected double targetY;
    protected Entity targetEntity;

    public Animal(double x, double y, double size, double speed, double maxHealth) {
        super(x, y, size);
        this.baseSpeed = speed;
        this.speed     = speed;
        this.targetX   = x;
        this.targetY   = y;
        this.maxHealth = maxHealth;
        this.health    = maxHealth;
    }

    /** Override trong subclass thủy sinh (Duck) để không bị giảm tốc trong Lake */
    protected boolean isAquatic() { return false; }

    // ── Abstract ──────────────────────────────────────────────────────────────

    protected abstract Animal createOffspring(double x, double y);

    // ── Strategy ──────────────────────────────────────────────────────────────

    public void setStrategy(SurvivalStrategy s) {
        this.strategy        = s;
        this.defaultStrategy = s;
    }

    @Override public void update() {}

    // ── Core Update ───────────────────────────────────────────────────────────

    public void updateSurvival(WorldMap map) {
        if (!isAlive) {
            if (deathTimer > 0) deathTimer--;
            return;
        }

        // Tốc độ theo trạng thái
        speed = (state == AnimalState.HUNTING || state == AnimalState.FLEEING)
                ? baseSpeed * 1.5 : baseSpeed;

        // Tốc độ theo địa hình (lấy modifier thấp nhất trong các Zone đang đứng)
        double terrainMod = 1.0;
        for (Zone z : map.getZones()) {
            if (z.contains(x, y)) {
                double mod = z.getSpeedModifier();
                // Sinh vật thủy sinh không bị giảm tốc trong nước
                if (z instanceof Lake && isAquatic()) mod = 1.0;
                terrainMod = Math.min(terrainMod, mod);
            }
        }
        speed *= terrainMod;

        // Đói / Khát
        hunger += Config.HUNGER_RATE;
        double tr = Config.THIRST_RATE;
        if (map.getCurrentSeason() == Season.SUMMER) tr *= Config.SUMMER_THIRST_MULTIPLIER;
        thirst += tr;

        // Mùa đông: mất HP khi không trong Forest
        if (map.getCurrentSeason() == Season.WINTER) {
            boolean sheltered = false;
            for (Zone z : map.getZones()) {
                if (z instanceof Forest && z.contains(x, y)) { sheltered = true; break; }
            }
            if (!sheltered) health -= Config.WINTER_OUTDOOR_DAMAGE;
        }

        // Chết
        if (hunger >= Config.MAX_HUNGER || thirst >= Config.MAX_THIRST || health <= 0) {
            die();
            return;
        }

        // Aggressive toggle
        if (!(defaultStrategy instanceof HunterStrategy)) {
            if (!isAggressive && hunger > Config.AGGRESSIVE_HUNGER_ON) {
                strategy     = new AggressiveStrategy();
                isAggressive = true;
            } else if (isAggressive && hunger < Config.AGGRESSIVE_HUNGER_OFF) {
                strategy     = defaultStrategy;
                isAggressive = false;
            }
        }

        if (strategy != null) strategy.executeBehavior(this, map);

        // Sinh sản
        if (map.getCurrentSeason() == Season.SPRING
                && hunger < 30 && thirst < 30
                && Math.random() < Config.REPRODUCE_CHANCE
                && map.getEntities().size() < Config.MAX_ENTITIES) {
            reproduce(map);
        }

        // Di chuyển (trừ trạng thái đứng yên)
        if (state != AnimalState.IDLE     &&
            state != AnimalState.SLEEPING &&
            state != AnimalState.EATING   &&
            state != AnimalState.DRINKING) {
            move(map);
        }
        // Reset YIELDING sau 1 frame (ngắn, chỉ để hiện label)
        if (state == AnimalState.YIELDING) state = AnimalState.IDLE;
    }

    // ── Movement ──────────────────────────────────────────────────────────────

    @Override
    public void move(WorldMap map) {
        double dx   = targetX - x;
        double dy   = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= speed) {
            x = targetX; y = targetY;
            if (state == AnimalState.WANDERING) state = AnimalState.IDLE;
            clampPosition(map);
            return;
        }

        double nx = dx / dist; // normalized direction
        double ny = dy / dist;

        // Repulsion: vật cản & động vật lớn hơn
        double rx = 0, ry = 0;
        for (Entity e : map.getEntities()) {
            if (e == this || !e.isAlive()) continue;
            boolean isObs  = e instanceof Obstacle;
            boolean isBigger = (e instanceof Animal) && e.getSize() > this.size;
            if (!isObs && !isBigger) continue;

            double d    = distanceTo(e);
            double safe = this.size / 2 + e.getSize() / 2 + 10;
            if (d < safe && d > 0.01) {
                double force = (safe - d) / safe;
                double mult  = (e instanceof Elephant) ? 4.0 : 2.0;
                rx += ((x - e.getX()) / d) * force * speed * mult;
                ry += ((y - e.getY()) / d) * force * speed * mult;
                // Hiển thị trạng thái nhường đường nếu bị đẩy bởi animal lớn hơn
                if (isBigger && state != AnimalState.FLEEING && state != AnimalState.HUNTING) {
                    state = AnimalState.YIELDING;
                }
            }
        }

        x += nx * speed + rx;
        y += ny * speed + ry;
        clampPosition(map);
    }

    private void clampPosition(WorldMap map) {
        x = Math.max(size / 2, Math.min(x, map.getWidth()  - size / 2));
        y = Math.max(size / 2, Math.min(y, map.getHeight() - size / 2));
    }

    @Override
    public void setTarget(double tx, double ty) { this.targetX = tx; this.targetY = ty; }

    // ── Survival Actions ──────────────────────────────────────────────────────

    public void eat(double nutrition) {
        hunger = Math.max(0, hunger - nutrition);
        state  = AnimalState.EATING;
    }

    public void drink(double amount) {
        thirst = Math.max(0, thirst - amount);
        state  = AnimalState.DRINKING;
    }

    public void takeDamage(double dmg) {
        health -= dmg;
        if (health <= 0) die();
    }

    protected void die() {
        isAlive    = false;
        state      = AnimalState.DEAD;
        deathTimer = Config.DEATH_LINGER_FRAMES;
    }

    public boolean isReadyToRemove() { return !isAlive && deathTimer <= 0; }

    protected void reproduce(WorldMap map) {
        double ox = Math.max(size, Math.min(x + 20, map.getWidth()  - size));
        double oy = Math.max(size, Math.min(y + 20, map.getHeight() - size));
        map.addEntity(createOffspring(ox, oy));
        hunger += 20;
    }

    // ── Helper: Lake gần nhất (dùng chung cho mọi Strategy) ─────────────────

    public Zone findNearestLake(WorldMap map) {
        Zone   best = null;
        double minD = Double.MAX_VALUE;
        for (Zone z : map.getZones()) {
            if (!(z instanceof Lake)) continue;
            double cx = z.getX() + z.getWidth()  / 2;
            double cy = z.getY() + z.getHeight() / 2;
            double dx = x - cx, dy2 = y - cy;
            double d  = Math.sqrt(dx * dx + dy2 * dy2);
            if (d < minD) { minD = d; best = z; }
        }
        return best;
    }

    // ── Render Helpers ────────────────────────────────────────────────────────

    /** 3 thanh HP / Hunger / Thirst trên đầu con vật */
    protected void renderStatusBars(GraphicsContext gc) {
        if (!isAlive) return;
        double bw = size * 1.4, bh = 3;
        double bx = x - bw / 2;
        double by = y - size / 2 - 14;

        // Nền mờ
        gc.setFill(Color.color(0, 0, 0, 0.28));
        gc.fillRoundRect(bx - 1, by - 1, bw + 2, bh * 3 + 6, 3, 3);

        // HP — đỏ
        gc.setFill(Color.web("#444444")); gc.fillRect(bx, by, bw, bh);
        gc.setFill(Color.web("#e74c3c")); gc.fillRect(bx, by, bw * clamp01(health / maxHealth), bh);

        // Hunger — cam (cao = nguy hiểm)
        gc.setFill(Color.web("#444444")); gc.fillRect(bx, by + bh + 1, bw, bh);
        gc.setFill(Color.web("#e67e22")); gc.fillRect(bx, by + bh + 1, bw * clamp01(hunger / Config.MAX_HUNGER), bh);

        // Thirst — xanh lam
        gc.setFill(Color.web("#444444")); gc.fillRect(bx, by + bh * 2 + 2, bw, bh);
        gc.setFill(Color.web("#2980b9")); gc.fillRect(bx, by + bh * 2 + 2, bw * clamp01(thirst / Config.MAX_THIRST), bh);
    }

    /** Nhãn nhỏ khi đang chạy trốn / săn mồi — giúp người xem hiểu ngay */
    protected void renderStateLabel(GraphicsContext gc) {
        if (!isAlive) return;
        String label = null;
        Color  col   = null;
        switch (state) {
            case FLEEING:  label = "!";   col = Color.YELLOW;         break;
            case HUNTING:  label = "⚔";  col = Color.web("#e74c3c"); break;
            case EATING:   label = "🍃"; col = Color.LIGHTGREEN;     break;
            case DRINKING: label = "💧"; col = Color.LIGHTSKYBLUE;   break;
            case YIELDING: label = "→";  col = Color.LIGHTGRAY;      break;
            default: break;
        }
        if (label != null) {
            gc.setFont(new Font("Arial", 10));
            gc.setFill(col);
            gc.fillText(label, x + size / 2 + 2, y - size / 2);
        }
    }

    /** Độ mờ của xác (1.0 → 0.0 trong DEATH_LINGER_FRAMES frame) */
    protected double getDeathAlpha() {
        return deathTimer / (double) Config.DEATH_LINGER_FRAMES;
    }

    private double clamp01(double v) { return Math.max(0, Math.min(v, 1)); }

    // ── Info cho popup click ──────────────────────────────────────────────────

    public String getInfo() {
        return String.format("%s | HP %.0f/%.0f | Đói %.0f%% | Khát %.0f%% | %s",
            getClass().getSimpleName(),
            health, maxHealth, hunger, thirst, state.name());
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public AnimalState getState()         { return state; }
    public void setState(AnimalState s)   { this.state = s; }
    public double getHealth()             { return health; }
    public double getHunger()             { return hunger; }
    public double getThirst()             { return thirst; }
    public double getVisionRange()        { return visionRange; }
    public double getSpeed()              { return speed; }
    public Entity getTargetEntity()       { return targetEntity; }
    public void setTargetEntity(Entity e) { this.targetEntity = e; }
    public double getMaxHealth()          { return maxHealth; }
}
