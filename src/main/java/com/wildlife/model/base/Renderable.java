package com.wildlife.model.base;

import javafx.scene.canvas.GraphicsContext;

/**
 * Interface cho các đối tượng có thể vẽ lên màn hình.
 */
public interface Renderable {
    void render(GraphicsContext gc);
}
