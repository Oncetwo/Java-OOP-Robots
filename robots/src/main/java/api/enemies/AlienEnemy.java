package api.enemies;

import api.*;
import java.awt.Color;
import java.awt.Graphics2D;

public class AlienEnemy implements IEnemy {

    private class AlienBehavior implements EnemyBehavior {
        private double x, y;
        private final double startX, startY;
        // Скорость пришельца (немного медленнее базового робота, чтобы был шанс убежать)
        private static final double SPEED = 0.045;

        public AlienBehavior(double startX, double startY) {
            this.startX = startX;
            this.startY = startY;
            this.x = startX;
            this.y = startY;
        }

        @Override
        public void update(GameContext context, RobotBehavior target) {
            double dt = context.getDeltaTime();
            // Вычисляем угол к роботу и летим прямо на него
            double angleToTarget = Math.atan2(target.getY() - y, target.getX() - x);
            x += Math.cos(angleToTarget) * SPEED * dt;
            y += Math.sin(angleToTarget) * SPEED * dt;
        }

        @Override
        public boolean isCatching(RobotBehavior target) {
            // Радиус захвата: если пришелец подлетел ближе чем на 15 пикселей к центру робота
            return Math.hypot(x - target.getX(), y - target.getY()) < 15;
        }

        @Override
        public void reset() {
            this.x = startX;
            this.y = startY;
        }

        @Override public double getX() { return x; }
        @Override public double getY() { return y; }
    }

    private class AlienVisualizer implements EnemyVisualizer {
        @Override
        public void draw(Graphics2D g, EnemyBehavior behavior) {
            int cx = (int) behavior.getX();
            int cy = (int) behavior.getY();

            // 1. Стеклянный купол (полупрозрачный голубой)
            g.setColor(new Color(0, 255, 255, 150));
            // Рисуем дугу (от 0 до 180 градусов - верхняя половинка круга)
            g.fillArc(cx - 10, cy - 15, 20, 20, 0, 180);

            // 2. Металлическая тарелка (основание)
            g.setColor(Color.LIGHT_GRAY);
            g.fillOval(cx - 18, cy - 5, 36, 12);
            g.setColor(Color.DARK_GRAY);
            g.drawOval(cx - 18, cy - 5, 36, 12); // Контур тарелки

            // 3. Зеленые бортовые огни пришельцев
            g.setColor(Color.GREEN);
            g.fillOval(cx - 12, cy - 2, 4, 4); // Левый
            g.fillOval(cx - 2, cy - 1, 4, 4);  // Центральный
            g.fillOval(cx + 8, cy - 2, 4, 4);  // Правый
        }
    }

    private final EnemyBehavior behavior;
    private final EnemyVisualizer visualizer = new AlienVisualizer();

    public AlienEnemy(double startX, double startY) {
        this.behavior = new AlienBehavior(startX, startY);
    }

    @Override public EnemyBehavior getBehavior() { return behavior; }
    @Override public EnemyVisualizer getVisualizer() { return visualizer; }
}