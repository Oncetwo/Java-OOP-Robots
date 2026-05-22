package api;

import api.enemies.AlienEnemy;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class GhostAndAlienTest {

    @Test
    void testAlienCatchLogic() {
        AlienEnemy alien = new AlienEnemy(100, 100);
        EnemyBehavior behavior = alien.getBehavior();

        //создаем фейковую цель близко
        RobotBehavior mockTarget = new RobotBehavior() {
            public void update(GameContext c) {}
            public double getX() { return 110; } // Дистанция 14 пикселей
            public double getY() { return 110; }
            public double getDirection() { return 0; }
            public void setPosition(double x, double y) {}
        };

        assertTrue(behavior.isCatching(mockTarget));

        //отодвигаем цель далеко
        RobotBehavior farTarget = new RobotBehavior() {
            public void update(GameContext c) {}
            public double getX() { return 500; }
            public double getY() { return 500; }
            public double getDirection() { return 0; }
            public void setPosition(double x, double y) {}
        };
        assertFalse(behavior.isCatching(farTarget));
    }

    @Test
    void testGhostRobotDashThroughWalls() {
        GhostRobot ghost = new GhostRobot();
        RobotBehavior behavior = ghost.getBehavior();
        //поставили перед стеной и юзаем пробел
        behavior.setPosition(50, 50);
        GameContext context = new GameContext() {
            public Point getMouseTarget() { return null; }
            public Set<Integer> getPressedKeys() {
                Set<Integer> keys = new HashSet<>();
                keys.add(KeyEvent.VK_SPACE);
                keys.add(KeyEvent.VK_W);
                return keys;
            }
            public int getFieldWidth() { return 800; }
            public int getFieldHeight() { return 800; }
            public double getDeltaTime() { return 10.0; }

            //ставим стену прямо по курсу робота
            public java.util.List<java.awt.Shape> getObstacles() {
                return Collections.singletonList(new Rectangle(40, 40, 20, 20));
            }
            public java.awt.Shape getFinishZone() { return null; }
        };

        double initialX = behavior.getX();

        behavior.update(context);
        assertTrue(behavior.getX() > initialX, "Должна была пройтись стена");
    }
}