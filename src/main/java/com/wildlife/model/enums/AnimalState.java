package com.wildlife.model.enums;

/**
 * Trạng thái hiện tại của một động vật
 */
public enum AnimalState {
    IDLE,       // Đứng yên
    WANDERING,  // Đi lang thang ngẫu nhiên
    HUNTING,    // Săn mồi (đuổi theo con mồi)
    FLEEING,    // Bỏ chạy khỏi kẻ thù
    EATING,     // Đang ăn
    DRINKING,   // Đang uống
    SLEEPING,   // Đang ngủ (hồi năng lượng)
    DEAD        // Đã chết
}
