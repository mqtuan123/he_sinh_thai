package com.wildlife.strategy;

import com.wildlife.app.Config;
import com.wildlife.model.animal.Animal;
import com.wildlife.model.base.Entity;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.environment.Zone;
import com.wildlife.model.environment.Forest;
import com.wildlife.model.obstacle.Bush;
import com.wildlife.model.enums.AnimalState;
import com.wildlife.sound.SoundManager;

/**
 * HunterStrategy — động vật ăn thịt (Sói, Hổ).
 *
 * Thứ tự ưu tiên:
 *   1. Khát > 60%  → tìm hồ uống (bỏ săn mồi)
 *   2. Đói  > 60%  → săn con mồi NGAY, tăng tốc, bỏ qua ẩn náu của con mồi
 *   3. Khát > 30%  → tìm hồ uống
 *   4. Đói  > 30%  → săn bình thường (con mồi ẩn trong Bush/Forest vẫn bỏ qua)
 *   5. No & không khát → lang thang
 */
public class HunterStrategy implements SurvivalStrategy {


    @Override
    public void executeBehavior(Animal animal, WorldMap map) {

        double hunger = animal.getHunger();
        double thirst = animal.getThirst();

        // ── 1. KHẨN CẤP: khát > 60% ─────────────────────────────────────────
        if (thirst > Config.SURVIVAL_URGENT) {
            if (seekWater(animal, map)) return;
        }

        // ── 2. KHẨN CẤP: đói > 60% — săn bất kể con mồi có đang ẩn không ───
        if (hunger > Config.SURVIVAL_URGENT) {
            if (huntPrey(animal, map, /* ignoreHiding= */ true)) return;
            // Không thấy con mồi: di chuyển dải rộng hơn
            animal.setState(AnimalState.WANDERING);
            double rx = Math.max(0, Math.min(animal.getX() + (Math.random() * 300 - 150), map.getWidth()));
            double ry = Math.max(0, Math.min(animal.getY() + (Math.random() * 300 - 150), map.getHeight()));
            animal.setTarget(rx, ry);
            return;
        }

        // ── 3. Bình thường: khát > 30% ───────────────────────────────────────
        if (thirst > Config.SURVIVAL_NORMAL) {
            if (seekWater(animal, map)) return;
        }

        // ── 4. Bình thường: đói > 30% — tôn trọng ẩn náu của con mồi ────────
        if (hunger > Config.SURVIVAL_NORMAL) {
            if (huntPrey(animal, map, /* ignoreHiding= */ false)) return;
        }

        // ── 5. No & không khát → lang thang ──────────────────────────────────
        if (animal.getState() == AnimalState.IDLE) {
            double rx = Math.max(0, Math.min(animal.getX() + (Math.random() * 100 - 50), map.getWidth()));
            double ry = Math.max(0, Math.min(animal.getY() + (Math.random() * 100 - 50), map.getHeight()));
            animal.setTarget(rx, ry);
            animal.setState(AnimalState.WANDERING);
        }
    }

    // ── Tìm nước ──────────────────────────────────────────────────────────────

    private boolean seekWater(Animal animal, WorldMap map) {
        Zone lake = animal.findNearestLake(map);
        if (lake == null) return false;
        if (lake.contains(animal.getX(), animal.getY())) {
            animal.drink(50);
            SoundManager.getInstance().playDrink();
            animal.setState(AnimalState.DRINKING);
        } else {
            animal.setTarget(lake.getX() + lake.getWidth() / 2,
                             lake.getY() + lake.getHeight() / 2);
            animal.setState(AnimalState.WANDERING);
        }
        return true;
    }

    // ── Săn mồi ───────────────────────────────────────────────────────────────

    /**
     * @param ignoreHiding  true = đói khẩn cấp, tấn công cả con mồi đang ẩn náu.
     */
    private boolean huntPrey(Animal animal, WorldMap map, boolean ignoreHiding) {
        Entity closestPrey = null;
        double minDist     = animal.getVisionRange() * (ignoreHiding ? 1.5 : 1.0);

        for (Entity e : map.getEntities()) {
            if (!(e instanceof Animal) || e == animal || !e.isAlive()) continue;
            Animal other = (Animal) e;
            if (other.getSize() >= animal.getSize()) continue;
            if (other.getClass().equals(animal.getClass())) continue;
            if (!ignoreHiding && isHiding(other, map)) continue;

            double dist = animal.distanceTo(other);
            if (dist < minDist) { minDist = dist; closestPrey = other; }
        }

        if (closestPrey == null) return false;

        if (animal.getState() != AnimalState.HUNTING) {
            SoundManager.getInstance().playHunt();
        }
        animal.setState(AnimalState.HUNTING);
        animal.setTarget(closestPrey.getX(), closestPrey.getY());

        double killRange = animal.getSize() / 2 + closestPrey.getSize() / 2 + 5;
        if (minDist < killRange) {
            animal.eat(50);
            SoundManager.getInstance().playEat();
            ((Animal) closestPrey).takeDamage(9999); // trigger die() → deathTimer + DEAD state
            SoundManager.getInstance().playDeath();
            animal.setState(AnimalState.EATING);
        }
        return true;
    }

    private boolean isHiding(Animal prey, WorldMap map) {
        for (Entity obs : map.getEntities()) {
            if (obs instanceof Bush && prey.distanceTo(obs) < obs.getSize()) return true;
        }
        for (Zone z : map.getZones()) {
            if (z instanceof Forest && z.contains(prey.getX(), prey.getY())) return true;
        }
        return false;
    }
}
