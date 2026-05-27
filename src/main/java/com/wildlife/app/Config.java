package com.wildlife.app;

/**
 * Chứa các cấu hình mặc định của game.
 */
public class Config {
    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;
    
    // UI Panel dimensions
    public static final int CONTROL_PANEL_WIDTH = 200;
    public static final int HUD_HEIGHT = 50;
    
    // Map dimensions
    public static final int MAP_WIDTH = WINDOW_WIDTH - CONTROL_PANEL_WIDTH;
    public static final int MAP_HEIGHT = WINDOW_HEIGHT - HUD_HEIGHT;

    public static final int FPS = 60;
    
    // System balancing
    public static final double MAX_HEALTH = 100.0;
    public static final double MAX_HUNGER = 100.0;
    public static final double MAX_THIRST = 100.0;
    public static final double HUNGER_RATE = 0.05; // Mức độ đói tăng lên mỗi frame
    public static final double THIRST_RATE = 0.08;
    public static final double PLANT_REGROW_RATE = 0.02; // Mức độ mọc lại của cây cỏ
    
    
    public static final double VISION_RANGE_DEFAULT = 150.0;
}
