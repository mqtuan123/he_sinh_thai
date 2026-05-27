package com.wildlife.model.plant;

import com.wildlife.model.base.Eatable;
import com.wildlife.model.base.Entity;
import com.wildlife.model.enums.Season;
import com.wildlife.app.Config;

/**
 * Lớp trừu tượng cho mọi loài thực vật.
 */
public abstract class Plant extends Entity implements Eatable {
    protected double nutritionValue;
    protected double maxNutrition;

    public Plant(double x, double y, double size, double nutritionValue) {
        super(x, y, size);
        this.nutritionValue = nutritionValue;
        this.maxNutrition = nutritionValue;
    }

    @Override
    public double getNutritionValue() {
        return nutritionValue;
    }

    public void updateSeason(Season season) {
        if (!isAlive) return;
        
        switch (season) {
            case SPRING:
                nutritionValue += Config.PLANT_REGROW_RATE * 2;
                if (nutritionValue > maxNutrition) nutritionValue = maxNutrition;
                break;
            case WINTER:
                // Chậm quá trình héo để cây không chết quá nhanh ở 60 FPS
                nutritionValue -= 0.02;
                if (nutritionValue <= 0) {
                    this.isAlive = false;
                }
                break;
            case SUMMER:
                break;
            case AUTUMN:
                nutritionValue -= 0.1;
                break;
        }
    }
    
    // Thực vật thường không có logic update phức tạp ngoại trừ việc bị ăn
    @Override
    public void update() {
        // Có thể thêm logic lớn lên theo thời gian ở đây
    }

    public double beEaten(double amount) {
        if (nutritionValue >= amount) {
            nutritionValue -= amount;
            return amount;
        } else {
            double eaten = nutritionValue;
            nutritionValue = 0;
            this.setAlive(false);
            return eaten;
        }
    }
}
