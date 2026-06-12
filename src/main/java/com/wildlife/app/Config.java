package com.wildlife.app;

/**
 * Toàn bộ hằng số của game tập trung tại đây.
 * Tách nhóm rõ ràng: Kích thước / Cân bằng / Mùa / Sinh sản / Render.
 */
public final class Config {

    private Config() {} // Utility class — không khởi tạo

    // ── Cửa sổ & Bố cục ──────────────────────────────────────────────────────
    public static final int WINDOW_WIDTH        = 1100;
    public static final int WINDOW_HEIGHT       = 720;
    public static final int CONTROL_PANEL_WIDTH = 180;
    public static final int HUD_HEIGHT          = 40;
    public static final int MAP_WIDTH           = WINDOW_WIDTH - CONTROL_PANEL_WIDTH;
    public static final int MAP_HEIGHT          = WINDOW_HEIGHT - HUD_HEIGHT;

    // ── Cân bằng sinh tồn ────────────────────────────────────────────────────
    public static final double MAX_HUNGER          = 100.0;
    public static final double MAX_THIRST          = 100.0;
    public static final double HUNGER_RATE         = 0.04;  // Tăng đói mỗi frame
    public static final double THIRST_RATE         = 0.07;  // Tăng khát cơ bản mỗi frame
    public static final double PLANT_REGROW_RATE   = 0.02;
    public static final double VISION_RANGE_DEFAULT = 150.0;

    // ── Mùa ──────────────────────────────────────────────────────────────────
    /** Mùa Hè: khát nước tăng 50% */
    public static final double SUMMER_THIRST_MULTIPLIER = 1.5;
    /** Mùa Đông: mất HP mỗi frame khi ở ngoài Forest */
    public static final double WINTER_OUTDOOR_DAMAGE    = 0.025;

    // ── Sinh sản ─────────────────────────────────────────────────────────────
    /** Số entity tối đa — vượt quá thì không sinh thêm */
    public static final int    MAX_ENTITIES        = 150;
    /** Xác suất sinh sản mỗi frame khi điều kiện đủ */
    public static final double REPRODUCE_CHANCE    = 0.0005;

    // ── Render ───────────────────────────────────────────────────────────────
    /** Số frame xác mờ dần sau khi chết (~2 giây ở 60 FPS) */
    public static final int    DEATH_LINGER_FRAMES = 120;
    // ── Ngưỡng sinh tồn (dùng chung cho mọi Strategy) ───────────────────────
    /** Khát/đói vượt ngưỡng này → ưu tiên tuyệt đối, bỏ qua sợ hãi / săn mồi */
    public static final double SURVIVAL_URGENT  = 60.0;
    /** Khát/đói vượt ngưỡng này → bắt đầu tìm ăn/uống bình thường */
    public static final double SURVIVAL_NORMAL  = 30.0;
    /** Ngưỡng kích hoạt AggressiveStrategy (đói > X) */
    public static final double AGGRESSIVE_HUNGER_ON  = 80.0;
    public static final double AGGRESSIVE_HUNGER_OFF = 40.0;
    /** Ngưỡng khát để đi tìm nước */
}
