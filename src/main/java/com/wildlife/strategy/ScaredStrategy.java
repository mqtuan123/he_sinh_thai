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
 * ScaredStrategy — động vật nhút nhát (Thỏ, Hươu).
 * Kế thừa PassiveStrategy cho hành vi ăn/uống.
 *
 * Thứ tự ưu tiên (quan trọng nhất ở đây: sinh tồn > trốn chạy):
 *   1. Khát > 60%  → tìm hồ NGAY (bỏ qua cả kẻ thù)
 *   2. Đói  > 60%  → tìm ăn NGAY  (bỏ qua cả kẻ thù)
 *   3. Thấy kẻ thù → FLEEING (tìm Bush/Forest để ẩn)
 *   4. Không thấy kẻ thù → PassiveStrategy (ăn/uống bình thường)
 *
 * Lý do: nếu đói/khát > 60% mà vẫn chỉ chạy, con vật sẽ chết đói/khát
 * trước khi bị kẻ thù bắt — không thực tế.
 */
public class ScaredStrategy extends PassiveStrategy {


    @Override
    public void executeBehavior(Animal animal, WorldMap map) {

        double hunger = animal.getHunger();
        double thirst = animal.getThirst();

        // ── 1 & 2. KHẨN CẤP: đói/khát > 60% — sinh tồn > bản năng sợ hãi ──
        // Con vật sẽ bất chấp kẻ thù để ăn/uống khi gần chết đói/khát
        if (thirst > Config.SURVIVAL_URGENT) {
            if (seekWater(animal, map)) return;
        }
        if (hunger > Config.SURVIVAL_URGENT) {
            if (seekFood(animal, map)) return;
            // Không thấy thức ăn → đi tìm rộng hơn
            animal.setState(AnimalState.WANDERING);
            double rx = clamp(animal.getX() + (Math.random() * 200 - 100), 0, map.getWidth());
            double ry = clamp(animal.getY() + (Math.random() * 200 - 100), 0, map.getHeight());
            animal.setTarget(rx, ry);
            return;
        }

        // ── Kiểm tra đang ẩn nấp ────────────────────────────────────────────
        boolean isHiding = checkHiding(animal, map);

        if (isHiding) {
            // Trong nơi ẩn: reset flee, ăn/uống bình thường nếu cần
            if (animal.getState() == AnimalState.FLEEING) animal.setState(AnimalState.IDLE);
            super.executeBehavior(animal, map);
            return;
        }

        // ── 3. Tìm kẻ thù gần nhất ──────────────────────────────────────────
        Entity closestPredator = null;
        double minPredatorDist = animal.getVisionRange();

        for (Entity e : map.getEntities()) {
            if (!(e instanceof Animal) || e == animal || !e.isAlive()) continue;
            Animal other = (Animal) e;
            if (other.getSize() <= animal.getSize()) continue;
            if (other.getState() == AnimalState.DEAD)  continue;

            double dist = animal.distanceTo(other);
            if (dist < minPredatorDist) { minPredatorDist = dist; closestPredator = other; }
        }

        if (closestPredator != null) {
            // ── 3a. Đang bị đe dọa — FLEE ────────────────────────────────────
            if (animal.getState() != AnimalState.FLEEING) {
                SoundManager.getInstance().playFlee();
            }
            animal.setState(AnimalState.FLEEING);
            flee(animal, closestPredator, map);
        } else {
            // ── 4. Không có kẻ thù → ăn/uống bình thường ────────────────────
            super.executeBehavior(animal, map);
        }
    }

    // ── Flee logic ────────────────────────────────────────────────────────────

    private void flee(Animal animal, Entity predator, WorldMap map) {
        double dx = animal.getX() - predator.getX();
        double dy = animal.getY() - predator.getY();

        // Ưu tiên 1: tìm Bush ẩn náu
        Entity closestBush = null;
        double minBushDist = animal.getVisionRange() * 1.5;
        for (Entity e : map.getEntities()) {
            if (e instanceof Bush) {
                double d = animal.distanceTo(e);
                if (d < minBushDist) { minBushDist = d; closestBush = e; }
            }
        }
        if (closestBush != null) {
            animal.setTarget(closestBush.getX(), closestBush.getY());
            return;
        }

        // Ưu tiên 2: tìm Forest gần nhất
        Zone nearestForest = null;
        double minForestDist = Double.MAX_VALUE;
        for (Zone z : map.getZones()) {
            if (z instanceof Forest) {
                double cx = z.getX() + z.getWidth()  / 2;
                double cy = z.getY() + z.getHeight() / 2;
                double d  = Math.sqrt((animal.getX() - cx) * (animal.getX() - cx)
                                    + (animal.getY() - cy) * (animal.getY() - cy));
                if (d < minForestDist) { minForestDist = d; nearestForest = z; }
            }
        }
        if (nearestForest != null) {
            animal.setTarget(nearestForest.getX() + nearestForest.getWidth()  / 2,
                             nearestForest.getY() + nearestForest.getHeight() / 2);
            return;
        }

        // Fallback: chạy ngược hướng kẻ thù
        double tx = clamp(animal.getX() + dx * 2, 0, map.getWidth());
        double ty = clamp(animal.getY() + dy * 2, 0, map.getHeight());
        animal.setTarget(tx, ty);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean checkHiding(Animal animal, WorldMap map) {
        for (Entity e : map.getEntities()) {
            if (e instanceof Bush && animal.distanceTo(e) < e.getSize()) return true;
        }
        for (Zone z : map.getZones()) {
            if (z instanceof Forest && z.contains(animal.getX(), animal.getY())) return true;
        }
        return false;
    }
}
