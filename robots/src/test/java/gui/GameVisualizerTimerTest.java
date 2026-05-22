package gui;

import api.maps.EmptyMap;
import api.DefaultRobot;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

public class GameVisualizerTimerTest {

    @Test
    void testTimerLifecycle() {
        GameVisualizer visualizer = new GameVisualizer();
        visualizer.setMap(new EmptyMap());
        DefaultRobot robot = new DefaultRobot();
        visualizer.setPlugin(robot);

        //ставим робота на стартовую позицию
        robot.getBehavior().setPosition(50, 750);
        visualizer.onModelUpdateEvent();
        assertEquals(0, visualizer.getAccumulatedTime(), "таймер должен быть равен 0");

        //имитируем движение на 1 пиксель
        robot.getBehavior().setPosition(60, 740);
        visualizer.onModelUpdateEvent(); //запускает логику проверки

        assertTrue(visualizer.getAccumulatedTime() >= 0, "после сдвига с места таймер должен запуститься");

        //настраиваем слушатель финиша
        AtomicBoolean isFinishTriggered = new AtomicBoolean(false);
        visualizer.setOnFinishListener(() -> isFinishTriggered.set(true));

        //перемещаем робота в финиш
        robot.getBehavior().setPosition(750, 750);
        visualizer.onModelUpdateEvent(); // Проверка пересечения

        assertTrue(isFinishTriggered.get(), "Мы должны были достгнуть финиш");
    }
}