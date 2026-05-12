package api;

import java.awt.Graphics2D;

import java.awt.Shape;
import java.util.List;

/**
 * Абстракция игровой карты
 * Содержит набор препятствий, через которые робот не может проехать
 */
public interface GameMap {
    String getName(); // Название карты для отображения в меню
    
    List<Shape> getObstacles(); // Получить список всех препятствий на карте (нужно для просчета столкновений)
    
    void draw(Graphics2D g); // Отрисовать карту (стены) на холсте

    default Shape getFinishZone() {
        return null;
    }
}