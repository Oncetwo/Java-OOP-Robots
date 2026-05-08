package api;

import java.awt.Point;
import java.util.Set;

public interface GameContext {
    // Текущая цель (координаты клика мыши) - для текущего поведения
    Point getMouseTarget();

    // Нажатые клавиши - ЗАДЕЛ НА БУДУЩЕЕ (п. 5 и 6)
    Set<Integer> getPressedKeys();

    // Размеры игрового поля - ЗАДЕЛ НА БУДУЩЕЕ (п. 8 - столкновения со стенами)
    int getFieldWidth();
    int getFieldHeight();

    // Время, прошедшее с прошлого кадра (для независимого от FPS движения)
    double getDeltaTime();

    // В будущем тут появится: Map getMap();
}