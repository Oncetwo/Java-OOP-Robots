package api;

import java.awt.Point;
import java.awt.Shape;
import java.util.List;
import java.util.Set;

public interface GameContext {
    // Текущая цель (координаты клика мыши) - для текущего поведения
    Point getMouseTarget();

    // Метод для получения списка кодов всех зажатых в данный момент клавиш
    Set<Integer> getPressedKeys();

    // Размеры игрового поля 
    int getFieldWidth();
    int getFieldHeight();

    // Время, прошедшее с прошлого кадра (для независимого от FPS движения)
    double getDeltaTime();
    
    List<Shape> getObstacles(); // получение списка препятсвий

    Shape getFinishZone();
}