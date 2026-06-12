package com.wildlife.model.enums;

/**
 * Chế độ "đặt entity" khi người dùng click lên bản đồ.
 * NONE = click không làm gì; các giá trị khác = spawn loại tương ứng.
 */
public enum SpawnMode {
    NONE,
    // Động vật
    RABBIT, DEER, WOLF, TIGER, ELEPHANT,
    // Thực vật
    GRASS, TREE,
    // Loài bổ sung
    DUCK,
    // Vật cản
    ROCK, BUSH
}
