package api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;

public class DefaultRobot implements IRobotPlugin {

    // 1. Внутренний класс логики (Модель)
    private class DefaultBehavior implements RobotBehavior {
        private double x = 100;
        private double y = 100;
        private double direction = 0;

        private static final double MAX_VELOCITY = 0.1;
        private static final double MAX_ANGULAR_VELOCITY = 0.001;

        @Override
        public void update(GameContext context) {
            Point target = context.getMouseTarget();
            if (target == null) return;

            double distance = distance(target.x, target.y, x, y);
            if (distance < 0.5) return; // Если достигли цели - стоим

            double angleToTarget = angleTo(x, y, target.x, target.y);
            double angleDifference = asNormalizedRadians(angleToTarget - direction);

            double velocity;
            double angularVelocity = 0;

            // Логика поворота и движения
            if (Math.abs(angleDifference) > 0.1) {
                velocity = MAX_VELOCITY * 0.3;
                angularVelocity = angleDifference > 0 ? MAX_ANGULAR_VELOCITY : -MAX_ANGULAR_VELOCITY;
            } else {
                velocity = MAX_VELOCITY;
                if (Math.abs(angleDifference) > 0.01) {
                    angularVelocity = angleDifference > 0 ? MAX_ANGULAR_VELOCITY * 0.5 : -MAX_ANGULAR_VELOCITY * 0.5;
                }
            }

            moveRobot(velocity, angularVelocity, context.getDeltaTime());
        }

        private void moveRobot(double velocity, double angularVelocity, double duration) {
            velocity = applyLimits(velocity, 0, MAX_VELOCITY);
            angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

            double newX = x + velocity / angularVelocity * (Math.sin(direction + angularVelocity * duration) - Math.sin(direction));
            if (!Double.isFinite(newX)) {
                newX = x + velocity * duration * Math.cos(direction);
            }

            double newY = y - velocity / angularVelocity * (Math.cos(direction + angularVelocity * duration) - Math.cos(direction));
            if (!Double.isFinite(newY)) {
                newY = y + velocity * duration * Math.sin(direction);
            }

            x = newX;
            y = newY;
            direction = asNormalizedRadians(direction + angularVelocity * duration);
        }

        // Вспомогательные математические методы
        private double distance(double x1, double y1, double x2, double y2) {
            return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        }
        private double angleTo(double fromX, double fromY, double toX, double toY) {
            return Math.atan2(toY - fromY, toX - fromX);
        }
        private double applyLimits(double value, double min, double max) {
            return Math.max(min, Math.min(value, max));
        }
        private double asNormalizedRadians(double angle) {
            while (angle <= -Math.PI) angle += 2*Math.PI;
            while (angle > Math.PI) angle -= 2*Math.PI;
            return angle;
        }

        // Геттеры для визуализатора
        @Override public double getX() { return x; }
        @Override public double getY() { return y; }
        @Override public double getDirection() { return direction; }
    }

    // 2. Внутренний класс отрисовки (Представление)
    private class DefaultVisualizer implements RobotVisualizer {
        @Override
        public void draw(Graphics2D g, RobotBehavior robot) {
            int robotCenterX = (int)(robot.getX() + 0.5);
            int robotCenterY = (int)(robot.getY() + 0.5);

            AffineTransform t = AffineTransform.getRotateInstance(robot.getDirection(), robotCenterX, robotCenterY);
            g.setTransform(t);

            // Рисуем корпус
            g.setColor(Color.MAGENTA);
            g.fillOval(robotCenterX - 15, robotCenterY - 5, 30, 10);
            g.setColor(Color.BLACK);
            g.drawOval(robotCenterX - 15, robotCenterY - 5, 30, 10);

            // Рисуем "нос" (направление)
            g.setColor(Color.WHITE);
            g.fillOval(robotCenterX + 10 - 2, robotCenterY - 2, 5, 5);
            g.setColor(Color.BLACK);
            g.drawOval(robotCenterX + 10 - 2, robotCenterY - 2, 5, 5);
        }
    }

    // 3. Сборка плагина
    private final RobotBehavior behavior = new DefaultBehavior();
    private final RobotVisualizer visualizer = new DefaultVisualizer();

    @Override
    public String getName() {
        return "Стандартный Робот";
    }

    @Override
    public RobotBehavior getBehavior() {
        return behavior;
    }

    @Override
    public RobotVisualizer getVisualizer() {
        return visualizer;
    }
}