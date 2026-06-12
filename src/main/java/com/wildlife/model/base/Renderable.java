package com.wildlife.model.base;

import javafx.scene.canvas.GraphicsContext;

/**
 * Interface Renderable — mọi thực thể có thể vẽ lên Canvas.
 * Tách biệt ViewLogic khỏi BioLogic theo yêu cầu OOP.
 * Áp dụng Interface Segregation Principle (ISP).
 */
public interface Renderable {
    void render(GraphicsContext gc);
}
