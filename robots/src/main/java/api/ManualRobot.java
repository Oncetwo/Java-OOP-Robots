package api;

import java.awt.Color; // для цвета робота
import java.awt.Graphics2D; // для рисования
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform; // для поворота робота при рисовании
import java.util.Set; // для множества зажатых клавиш

/**
 * Плагин робота с ручным управлением (WASD / Стрелки)
 * Логика движения независима от остального кода 
 */
public class ManualRobot implements IRobotPlugin {

	private class ManualBehavior implements RobotBehavior {
        private double x = 50; // координаты робота и его направление
        private double y = 750;
        private double direction = 0; // угол поворта в радианах
        
        // Характеристики робота 
        private static final double SPEED = 0.15; 
        private static final double ROTATION_SPEED = 0.005; // константа угловой скорости

        @Override
        public void update(GameContext context) {
            Set<Integer> keys = context.getPressedKeys(); // множество нажатых клавиш
            double dt = context.getDeltaTime(); // время, прошедшее с прошлого обновления

            // Обработка поворота (Влево/Вправо)
            if (keys.contains(KeyEvent.VK_A) || keys.contains(KeyEvent.VK_LEFT)) { // проверяем есть ли кнопка в списке нажатых клавиш
                direction -= ROTATION_SPEED * dt; // считаем угол поворота как угловая скорость на время, прошедшее с последнего обновления
            }
            if (keys.contains(KeyEvent.VK_D) || keys.contains(KeyEvent.VK_RIGHT)) {
                direction += ROTATION_SPEED * dt;
            }

            // промежуточные переменные dx и dy для расчета потенциального шага
            double dx = 0;
            double dy = 0;

            // Обработка движения (Вверх/Вниз)
            // Если зажаты W и D, робот будет одновременно и ехать, и поворачивать 
            if (keys.contains(KeyEvent.VK_W) || keys.contains(KeyEvent.VK_UP)) {
                //  записываем желаемое смещение в dx и dy 
                dx = SPEED * dt * Math.cos(direction);
                dy = SPEED * dt * Math.sin(direction);
            }
            if (keys.contains(KeyEvent.VK_S) || keys.contains(KeyEvent.VK_DOWN)) {
                // ИЗМЕНЕНО: Аналогично для движения назад
                dx = -SPEED * dt * Math.cos(direction);
                dy = -SPEED * dt * Math.sin(direction);
            }
            
            // Проверяем X и Y по отдельности, чтобы робот мог скользить вдоль стен, а не застревать в них
            
            // Пробуем сдвинуться по горизонтали
            double nextX = x + dx;
            if (!isColliding(nextX, y, context.getObstacles())) {
                x = nextX;
            }

            // Пробуем сдвинуться по вертикали
            double nextY = y + dy;
            if (!isColliding(x, nextY, context.getObstacles())) {
                y = nextY;
            }
            
            // Проверяем и ограничиваем координату X
            if (x < 0) {
                // Если робот уехал левее левого края экрана, возвращаем его на 0
                x = 0;
            } else if (x > 800) {
                // Если робот уехал правее правого края экрана, ставим его на границу поля
                x = 800;
            }

            // Проверяем и ограничиваем координату Y
            if (y < 0) {
                // Если робот уехал выше верхнего края экрана, возвращаем его на 0
                y = 0;
            } else if (y > 800) {
                // Если робот уехал ниже нижнего края экрана, ставим его на нижнюю границу
                y = 800;
            }
        }

        // метод для проверки пересечения хитбокса робота с препятствиями карты
        private boolean isColliding(double testX, double testY, java.util.List<java.awt.Shape> obstacles) {
            // Создаем виртуальную область вокруг проверяемой точки
            java.awt.geom.Rectangle2D robotHitbox = new java.awt.geom.Rectangle2D.Double(testX - 10, testY - 10, 15, 15);
            
            for (java.awt.Shape wall : obstacles) {
                // Если любая из фигур (стен) на карте пересекает хитбокс
                if (wall.intersects(robotHitbox)) {
                    return true; // Столкновение
                }
            }
            return false; // Путь свободен
        }

        @Override public double getX() { return x; }
        @Override public double getY() { return y; }
        @Override public double getDirection() { return direction; }
        
        @Override
        public void setPosition(double x, double y) { // метод установки позиции робота (для телепортации в начало)
            this.x = x;
            this.y = y;
        }
    }

    
    private class ManualVisualizer implements RobotVisualizer {
        @Override
        public void draw(Graphics2D g, RobotBehavior robot) {
            int cx = (int)(robot.getX() + 0.5); // запрашиваем текущие координаты и +0,5 для правильного округления до цело
            int cy = (int)(robot.getY() + 0.5); // так как экран состоит из целых пикселей (не дробных)

            // Сохраняем старую трансформацию, чтобы не испортить весь холст
            AffineTransform old = g.getTransform();
            
            g.rotate(robot.getDirection(), cx, cy); // поворачиваем весь холст на угол, который вернул плагин

            // Рисуем треугольный корпус робота
            int[] px = {cx - 18, cx + 28, cx - 18};
            int[] py = {cy - 18, cy, cy + 18};
            
            g.setColor(Color.ORANGE);
            g.fillPolygon(px, py, 3); // закрашиваем внутреннюю часть треугольника оранжевым цветом
            g.setColor(Color.BLACK);
            g.drawPolygon(px, py, 3); // рисуем черный контур

            // Восстанавливаем трансформацию
            g.setTransform(old);
        }
    }

    private final RobotBehavior behavior = new ManualBehavior();
    private final RobotVisualizer visualizer = new ManualVisualizer();

    @Override public String getName() { return "Ручной Робот (WASD)"; }
    @Override public RobotBehavior getBehavior() { return behavior; }
    @Override public RobotVisualizer getVisualizer() { return visualizer; }
}




