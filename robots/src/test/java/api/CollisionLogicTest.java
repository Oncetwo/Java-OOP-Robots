package api;

import org.junit.jupiter.api.Test;
import java.awt.geom.Rectangle2D;
import static org.junit.jupiter.api.Assertions.*;

public class CollisionLogicTest {

    @Test
    public void testRobotHitsWall() {
        // 1. Создаем стену (препятствие)
        // x=100, y=100, ширина=50, высота=50
        Rectangle2D wall = new Rectangle2D.Double(100, 100, 50, 50);

        // 2. Создаем хитбокс робота, который "наехал" на стену (пересекается)
        Rectangle2D robotHitboxColliding = new Rectangle2D.Double(90, 90, 20, 20); // Задевает левый верхний угол стены

        // 3. Создаем хитбокс робота, который стоит далеко
        Rectangle2D robotHitboxSafe = new Rectangle2D.Double(10, 10, 20, 20);

        // Проверяем логику java.awt.geom (которую мы используем для игры)
        assertTrue(wall.intersects(robotHitboxColliding), "Система должна регистрировать столкновение (true)");
        assertFalse(wall.intersects(robotHitboxSafe), "Если робот далеко от стены, столкновения быть не должно (false)");
    }

    @Test
    public void testRobotHitsFinishLine() {
        // Имитируем золотой прямоугольник финиша
        Rectangle2D finishZone = new Rectangle2D.Double(700, 700, 40, 40);

        // Имитируем робота, который приехал на финиш
        Rectangle2D robotOnFinish = new Rectangle2D.Double(710, 710, 20, 20);

        assertTrue(finishZone.intersects(robotOnFinish), "Логика финиша должна сработать при пересечении");
    }
}