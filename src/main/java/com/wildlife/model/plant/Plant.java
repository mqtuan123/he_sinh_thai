package com.wildlife.model.plant;

import com.wildlife.app.Config;
import com.wildlife.model.base.Eatable;
import com.wildlife.model.base.Entity;
import com.wildlife.model.enums.Season;

/**
 * Lớp trừu tượng cho mọi loài thực vật.
 * Fix: beEaten() kiểm tra isAlive trước — tránh Animal ăn cây đã chết cùng frame.
 * Thêm: canBeEaten() helper — dùng trong Strategy để kiểm tra trước khi move đến.
 */
public abstract class Plant extends Entity implements Eatable {

    protected double nutritionValue;
    protected double maxNutrition;

    public Plant(double x, double y, double size, double nutritionValue) {
        super(x, y, size);
        this.nutritionValue = nutritionValue;
        this.maxNutrition   = nutritionValue;
    }

    @Override
    public double getNutritionValue() { return nutritionValue; }

    /** True khi còn sống và còn ít nhất 1 đơn vị dinh dưỡng */
    public boolean canBeEaten() { return isAlive && nutritionValue > 0; }

    public void updateSeason(Season season) {
        if (!isAlive) return;
        switch (season) {
            case SPRING:
                nutritionValue = Math.min(nutritionValue + Config.PLANT_REGROW_RATE * 2, maxNutrition);
                break;
            case WINTER:
                nutritionValue -= 0.02;
                if (nutritionValue <= 0) this.isAlive = false;
                break;
            case AUTUMN:
                nutritionValue -= 0.005;
                if (nutritionValue <= 0) this.isAlive = false;
                break;
            case SUMMER:
                break;
        }
    }

    /**
     * Trả về lượng dinh dưỡng thực sự lấy được.
     * Fix: kiểm tra isAlive trước — tránh ăn cây đã chết cùng frame.
     */
    public double beEaten(double amount) {
        if (!isAlive || nutritionValue <= 0) return 0;
        double eaten = Math.min(amount, nutritionValue);
        nutritionValue -= eaten;
        if (nutritionValue <= 0) this.setAlive(false);
        return eaten;
    }

    @Override
    public void update() {}
}
