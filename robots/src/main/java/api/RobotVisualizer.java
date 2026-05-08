package api;

import java.awt.Graphics2D;

public interface RobotVisualizer {
    // Передаем поведение, чтобы визуализатор знал, где рисовать (координаты X, Y)
    void draw(Graphics2D g, RobotBehavior robot);
}