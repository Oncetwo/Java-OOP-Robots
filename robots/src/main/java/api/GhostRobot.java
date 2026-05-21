package api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.Set;

public class GhostRobot implements IRobotPlugin {

    private class GhostBehavior implements RobotBehavior {
        private double x = 50, y = 750, direction = 0;

        // Базовые характеристики
        private static final double NORMAL_SPEED = 0.15;
        private static final double BOOST_SPEED = 0.25;
        private static final double DASH_SPEED = 0.8;
        private static final double ROTATION_SPEED = 0.005;

        // Таймеры (в миллисекундах)
        private double dashTimer = 0;
        private double boostTimer = 0;
        private double cooldownTimer = 0;

        private static final double DASH_DURATION = 150;  // Длительность рывка
        private static final double BOOST_DURATION = 800; // Длительность ускорения после рывка
        private static final double COOLDOWN_DURATION = 3000; // КД способности (3 секунды)

        @Override
        public void update(GameContext context) {
            Set<Integer> keys = context.getPressedKeys();
            double dt = context.getDeltaTime();

            // Обновляем таймеры
            if (dashTimer > 0) dashTimer -= dt;
            else if (boostTimer > 0) boostTimer -= dt;
            if (cooldownTimer > 0) cooldownTimer -= dt;

            // Активация способности (ПРОБЕЛ)
            if (keys.contains(KeyEvent.VK_SPACE) && cooldownTimer <= 0) {
                dashTimer = DASH_DURATION;
                boostTimer = BOOST_DURATION;
                cooldownTimer = COOLDOWN_DURATION;
            }

            // Управление поворотом (во время рывка поворачивать нельзя)
            if (dashTimer <= 0) {
                if (keys.contains(KeyEvent.VK_A) || keys.contains(KeyEvent.VK_LEFT)) direction -= ROTATION_SPEED * dt;
                if (keys.contains(KeyEvent.VK_D) || keys.contains(KeyEvent.VK_RIGHT)) direction += ROTATION_SPEED * dt;
            }

            // Определяем текущую скорость
            double currentSpeed = NORMAL_SPEED;
            if (dashTimer > 0) currentSpeed = DASH_SPEED;
            else if (boostTimer > 0) currentSpeed = BOOST_SPEED;

            double dx = 0, dy = 0;

            // Движение (во время рывка робот летит вперед сам, иначе ждет WASD)
            if (dashTimer > 0 || keys.contains(KeyEvent.VK_W) || keys.contains(KeyEvent.VK_UP)) {
                dx = currentSpeed * dt * Math.cos(direction);
                dy = currentSpeed * dt * Math.sin(direction);
            } else if (keys.contains(KeyEvent.VK_S) || keys.contains(KeyEvent.VK_DOWN)) {
                dx = -currentSpeed * dt * Math.cos(direction);
                dy = -currentSpeed * dt * Math.sin(direction);
            }

            double nextX = x + dx;
            double nextY = y + dy;

            // ЛОГИКА РЫВКА: если dashTimer > 0, мы игнорируем стены (isColliding не вызывается)
            boolean isGhost = (dashTimer > 0);

            if (isGhost || !isColliding(nextX, y, context.getObstacles())) x = nextX;
            if (isGhost || !isColliding(x, nextY, context.getObstacles())) y = nextY;

            // Ограничения границ экрана остаются всегда (чтобы не улететь за карту)
            x = Math.max(0, Math.min(x, context.getFieldWidth()));
            y = Math.max(0, Math.min(y, context.getFieldHeight()));
        }

        private boolean isColliding(double testX, double testY, java.util.List<java.awt.Shape> obstacles) {
            java.awt.geom.Rectangle2D hitbox = new java.awt.geom.Rectangle2D.Double(testX - 10, testY - 10, 15, 15);
            for (java.awt.Shape wall : obstacles) {
                if (wall.intersects(hitbox)) return true;
            }
            return false;
        }

        @Override public double getX() { return x; }
        @Override public double getY() { return y; }
        @Override public double getDirection() { return direction; }
        @Override
        public void setPosition(double x, double y) {
            this.x = x;
            this.y = y;

            // === ИЗМЕНЕНИЕ: Полный сброс состояния при телепортации на старт ===
            this.direction = 0;     // Сбрасываем угол поворота (смотрит вправо)
            this.dashTimer = 0;     // Сбрасываем активный рывок
            this.boostTimer = 0;    // Сбрасываем активное ускорение
            this.cooldownTimer = 0; // Обнуляем кулдаун способности
        }

        // Геттеры для отрисовки кулдауна
        public double getCooldownPercent() {
            if (cooldownTimer <= 0) return 1.0;
            return 1.0 - (cooldownTimer / COOLDOWN_DURATION);
        }
        public boolean isDashing() { return dashTimer > 0; }
        public boolean isBoosting() { return boostTimer > 0; }
    }

    private class GhostVisualizer implements RobotVisualizer {
        @Override
        public void draw(Graphics2D g, RobotBehavior robot) {
            GhostBehavior b = (GhostBehavior) robot;
            int cx = (int) (b.getX() + 0.5);
            int cy = (int) (b.getY() + 0.5);

            AffineTransform old = g.getTransform();
            g.rotate(b.getDirection(), cx, cy);

            // Меняем цвет в зависимости от состояния
            if (b.isDashing()) g.setColor(new Color(0, 255, 255, 150)); // Полупрозрачный голубой (призрак)
            else if (b.isBoosting()) g.setColor(Color.CYAN); // Яркий голубой
            else g.setColor(Color.BLUE); // Обычный синий

            int[] px = {cx - 18, cx + 28, cx - 18};
            int[] py = {cy - 18, cy, cy + 18};
            g.fillPolygon(px, py, 3);

            g.setColor(Color.WHITE);
            g.drawPolygon(px, py, 3);
            g.setTransform(old);

            // ОТРИСОВКА ПОЛОСКИ КУЛДАУНА (над роботом)
            int barWidth = 30;
            int barHeight = 4;
            g.setColor(Color.RED);
            g.fillRect(cx - barWidth/2, cy - 25, barWidth, barHeight);
            g.setColor(Color.GREEN);
            g.fillRect(cx - barWidth/2, cy - 25, (int)(barWidth * b.getCooldownPercent()), barHeight);
        }
    }

    private final RobotBehavior behavior = new GhostBehavior();
    private final RobotVisualizer visualizer = new GhostVisualizer();

    @Override public String getName() { return "Призрачный Робот (Рывок - Пробел)"; }
    @Override public RobotBehavior getBehavior() { return behavior; }
    @Override public RobotVisualizer getVisualizer() { return visualizer; }
}