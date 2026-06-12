package com.wildlife.strategy;

import com.wildlife.app.Config;
import com.wildlife.model.animal.Animal;
import com.wildlife.model.base.Entity;
import com.wildlife.model.environment.WorldMap;
import com.wildlife.model.environment.Zone;
import com.wildlife.model.plant.Plant;
import com.wildlife.model.enums.AnimalState;
import com.wildlife.sound.SoundManager;

/**
 * PassiveStrategy — động vật ăn thực vật (Thỏ, Hươu, Voi, Vịt).
 *
 * Thứ tự ưu tiên (survival hierarchy):
 *   1. Khát > 60%  → tìm hồ uống nước  (URGENT)
 *   2. Đói  > 60%  → tìm thức ăn       (URGENT, bỏ qua sợ hãi)
 *   3. Khát > 30%  → tìm hồ uống nước  (bình thường)
 *   4. Đói  > 30%  → tìm thức ăn       (bình thường)
 *   5. An toàn     → lang thang
 *
 * Thực thể ưu tiên thức ăn: Elephant → FruitTree trước, Duck → ăn trong Lake.
 */
public class PassiveStrategy implements SurvivalStrategy {

    // ── Ngưỡng sinh tồn ──────────────────────────────────────────────────────

    @Override
    public void executeBehavior(Animal animal, WorldMap map) {

        double hunger = animal.getHunger();
        double thirst = animal.getThirst();

        // ── 1. KHẨN CẤP: khát > 60% — bỏ mọi thứ, chạy đến hồ ngay ─────────
        if (thirst > Config.SURVIVAL_URGENT) {
            if (seekWater(animal, map)) return;
        }

        // ── 2. KHẨN CẤP: đói > 60% — bỏ mọi thứ, tìm ăn ngay ──────────────
        if (hunger > Config.SURVIVAL_URGENT) {
            if (seekFood(animal, map)) return;
            // Nếu không thấy thức ăn → vẫn cần đi tìm, không lang thang
            animal.setState(AnimalState.WANDERING);
            double rx = clamp(animal.getX() + (Math.random() * 200 - 100), 0, map.getWidth());
            double ry = clamp(animal.getY() + (Math.random() * 200 - 100), 0, map.getHeight());
            animal.setTarget(rx, ry);
            return;
        }

        // ── 3. Bình thường: khát > 30% → tìm hồ ────────────────────────────
        if (thirst > Config.SURVIVAL_NORMAL) {
            if (seekWater(animal, map)) return;
        }

        // ── 4. Bình thường: đói > 30% → tìm ăn ─────────────────────────────
        if (hunger > Config.SURVIVAL_NORMAL) {
            if (seekFood(animal, map)) return;
        }

        // ── 5. No và không khát → lang thang ────────────────────────────────
        if (animal.getState() == AnimalState.IDLE) {
            double rx = clamp(animal.getX() + (Math.random() * 100 - 50), 0, map.getWidth());
            double ry = clamp(animal.getY() + (Math.random() * 100 - 50), 0, map.getHeight());
            animal.setTarget(rx, ry);
            animal.setState(AnimalState.WANDERING);
        }
    }

    // ── Tìm nước ──────────────────────────────────────────────────────────────

    protected boolean seekWater(Animal animal, WorldMap map) {
        Zone nearestLake = animal.findNearestLake(map);
        if (nearestLake == null) return false;

        if (nearestLake.contains(animal.getX(), animal.getY())) {
            animal.drink(50);
            SoundManager.getInstance().playDrink();
            animal.setState(AnimalState.DRINKING);
        } else {
            animal.setState(AnimalState.WANDERING);
            animal.setTarget(
                nearestLake.getX() + nearestLake.getWidth()  / 2,
                nearestLake.getY() + nearestLake.getHeight() / 2
            );
        }
        return true;
    }

    // ── Tìm thức ăn ──────────────────────────────────────────────────────────

    /**
     * Tìm Plant gần nhất trong tầm nhìn và di chuyển đến / ăn.
     * Subclass override để ưu tiên loại thức ăn khác (Elephant → FruitTree).
     * @return true nếu đã tìm thấy mục tiêu
     */
    protected boolean seekFood(Animal animal, WorldMap map) {
        Entity bestFood    = null;
        double minDistance = animal.getVisionRange();

        for (Entity e : map.getEntities()) {
            if (!(e instanceof Plant) || !e.isAlive()) continue;
            Plant p = (Plant) e;
            if (!p.canBeEaten()) continue;

            double dist  = animal.distanceTo(e);
            double score = scoreFood(animal, e, dist); // subclass có thể override
            if (score < minDistance) { minDistance = score; bestFood = e; }
        }

        if (bestFood == null) return false;

        animal.setState(AnimalState.HUNTING);
        animal.setTarget(bestFood.getX(), bestFood.getY());

        double eatRange = animal.getSize() / 2 + bestFood.getSize() / 2 + 5;
        if (animal.distanceTo(bestFood) < eatRange) {
            double nutrition = ((Plant) bestFood).beEaten(20);
            animal.eat(nutrition);
            SoundManager.getInstance().playEat();
            animal.setState(AnimalState.EATING);
        }
        return true;
    }

    /**
     * Điểm ưu tiên thức ăn — số càng nhỏ càng được ưu tiên.
     * Mặc định: khoảng cách thuần túy.
     * Subclass override để bonus/penalty theo loại thức ăn.
     */
    protected double scoreFood(Animal animal, Entity food, double dist) {
        return dist;
    }

    // ── Util ──────────────────────────────────────────────────────────────────

    protected double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(v, max));
    }
}
