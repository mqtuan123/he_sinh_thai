package com.wildlife.model.base;

/**
 * Interface Eatable — thực thể có thể bị ăn.
 * Plant implements Eatable; Animal không implements (không ăn thịt qua interface này).
 * Minh họa Interface Segregation: tách hành vi "bị ăn" riêng.
 */
public interface Eatable {
    double getNutritionValue();
}
