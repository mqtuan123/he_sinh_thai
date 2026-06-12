package com.wildlife.model.enums;

/**
 * Trạng thái hành vi của động vật — dùng trong Strategy và Render.
 *
 * IDLE      : Đứng yên / chờ đợi
 * WANDERING : Lang thang tìm thức ăn
 * HUNTING   : Đang săn mồi / lao vào thức ăn (kẻ ăn thịt)
 * FLEEING   : Đang chạy trốn (con mồi)
 * EATING    : Đang ăn
 * DRINKING  : Đang uống nước
 * SLEEPING  : Đang ngủ (nghỉ ngơi)
 * YIELDING  : Đang dạt sang một bên nhường đường cho động vật lớn hơn
 * DEAD      : Đã chết (đang trong giai đoạn xác mờ dần)
 */
public enum AnimalState {
    IDLE,
    WANDERING,
    HUNTING,
    FLEEING,
    EATING,
    DRINKING,
    SLEEPING,
    YIELDING,
    DEAD
}
