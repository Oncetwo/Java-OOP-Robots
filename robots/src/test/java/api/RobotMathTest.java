package api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RobotMathTest {
    @Test
    public void testDistanceCalculation() {
        // Дистанция от (0,0) до (3,4) по теореме Пифагора должна быть 5
        double dist = distance(0, 0, 3, 4);
        assertEquals(5.0, dist, 0.001, "Расчет дистанции неверен");
    }

    @Test
    public void testAngleCalculation() {
        // Угол от (0,0) до (10,10) должен быть 45 градусов (в радианах это PI/4)
        double angle = angleTo(0, 0, 10, 10);
        assertEquals(Math.PI / 4, angle, 0.001, "Расчет угла неверен");
    }

    @Test
    public void testAngleNormalization() {
        // Нормализация должна приводить углы больше PI в отрицательные
        double normalized = asNormalizedRadians(Math.PI + 0.1);
        assertTrue(normalized < 0, "Угол больше PI должен стать отрицательным");
    }

    // --- Копии ваших математических методов для примера (чтобы тест компилировался) ---
    private double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private double angleTo(double x1, double y1, double x2, double y2) {
        return Math.atan2(y2 - y1, x2 - x1);
    }

    private double asNormalizedRadians(double angle) {
        while (angle < -Math.PI) angle += 2.0 * Math.PI;
        while (angle >= Math.PI) angle -= 2.0 * Math.PI;
        return angle;
    }
}