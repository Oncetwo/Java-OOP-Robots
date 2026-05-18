package gui;

import api.GameContext;

import api.GameMap;
import api.IRobotPlugin;
import api.DefaultRobot;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.geom.Rectangle2D;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer(); // основной таймер для генерации событий

    private static Timer initTimer() { // метод для создания таймер демона
        return new Timer("events generator", true); // имя таймера, флаг который говорит, что таймер демон
    }

    private IRobotPlugin currentRobot = new DefaultRobot(); // поле для хранение текущего активного робота (стандартный по умолчанию)
    private Point mouseTarget = null; // по умолчанию пусть стоит

    // Хранилище для кодов зажатых клавиш (Set гарантирует отсутствие дублей)
    private final Set<Integer> pressedKeys = new HashSet<>();
    
    private boolean isTimerRunning = false; // поля таймера прохождения карты
    private boolean isFinished = false;
    private long startTime = 0;
    
    private java.util.function.Consumer<Long> timeListener; // слушатель, который будет отправлять время в окно таймера

    public void setTimeListener(java.util.function.Consumer<Long> listener) {
        this.timeListener = listener;
    }
    
    private GameMap currentMap = new api.maps.EmptyMap(); // поле карты - по умолчанию - пустая карта
    
    private static final int VIRTUAL_WIDTH = 800; // константы, которые будут определять размер виртуального мира
    private static final int VIRTUAL_HEIGHT = 800;

    public GameVisualizer() {
        // Таймер перерисовки (60 FPS примерно)
        m_timer.schedule(new TimerTask() { // планируем выполнение задачи (создаем объект задачи)
            @Override
            public void run() { EventQueue.invokeLater(GameVisualizer.this::repaint); }
        }, 0, 16); // класс свинг управляющий потоками, берем текущий объект и просим перерисовать компонент)

        // Таймер обновления логики каждые 10 мс вызываем расчет координат
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() { onModelUpdateEvent(); }
        }, 0, 10);

        // слушатель мыши для DefaultRobot
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // теперь мы не берем координаты напрямую, а пересчитываем их через метод-переводчик
                onMouseClick(e.getPoint()); 
                repaint(); // просим перерисовать экран
            }
        });

        setupKeyBindings(); // настраиваем урпавление клавиатурой   
        setDoubleBuffered(true); // двойная буферизация (чтобы картинка не мерцала)
        setFocusable(true); // Чтобы компонент мог принимать события клавиш
    }

    /**
     * Настройка Key Bindings
     * Мы связываем физическую клавишу с логическим действием (нажата/отпущена)
     */
    private void setupKeyBindings() { // список клавиш, которые будем слушать
        int[] keys = {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, 
                      KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT};

        for (int keyCode : keys) {
            // Действие при нажатии (таблица (карта-ввода) что нажал пользователь - название действия)
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0, false), "press" + keyCode);
            getActionMap().put("press" + keyCode, new AbstractAction() { // карта действий (название действия - что делать))
                @Override // AbstractAction - класс для создания действий
                public void actionPerformed(ActionEvent e) {
                    pressedKeys.add(keyCode); // (метод вызовется, когда действие сработает)
                }
            });

            // Действие при отпускании
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0, true), "release" + keyCode);
            getActionMap().put("release" + keyCode, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pressedKeys.remove(keyCode);
                }
            });
        }
    }

    public void setPlugin(IRobotPlugin plugin) { // установка нового плагина
        if (plugin != null) {
            this.currentRobot = plugin;
        }
    }

    protected void onModelUpdateEvent() { // обновление состояния робота
        if (currentRobot == null || currentRobot.getBehavior() == null) {
        	return;
        }

        // Создаем контекст, в который теперь реально передаем зажатые клавиши
        GameContext context = new GameContext() {
            @Override public Point getMouseTarget() { return mouseTarget; } // координаты последнего клика мышки
            
            // Отправляем текущее состояние клавиатуры плагину (копия множества зажатых клавиш)
            @Override public Set<Integer> getPressedKeys() { return new HashSet<>(pressedKeys); } 
            
            // сообщаем роботу ширину и высоту именно виртуального мира, а не окна в пикселях
            @Override public int getFieldWidth() { return VIRTUAL_WIDTH; } 
            @Override public int getFieldHeight() { return VIRTUAL_HEIGHT; }
            
            @Override public double getDeltaTime() { return 10.0; } // время, прошедшее с последнего обновления
            
            @Override 
            public java.util.List<java.awt.Shape> getObstacles() { 
                // Передаем список стен из текущей карты в контекст робота
                return currentMap != null ? currentMap.getObstacles() : java.util.Collections.emptyList(); 
            }
            @Override
            public java.awt.Shape getFinishZone() {
                return currentMap != null ? currentMap.getFinishZone() : null;
            }
        };

        currentRobot.getBehavior().update(context);
        
        // Логика таймера и финиша
        double rx = currentRobot.getBehavior().getX();
        double ry = currentRobot.getBehavior().getY();

        // Условие старта: таймер не запущен, игра не завершена, карта существует, финиш существует 
        if (!isTimerRunning && !isFinished && currentMap != null && currentMap.getFinishZone() != null) { 
            if (Math.abs(rx - 50) > 1 || Math.abs(ry - (VIRTUAL_HEIGHT - 50)) > 1) { // робот сдвинулся с места (Расстояние по X или Y от точки 50 больше 1 пикселя)
                isTimerRunning = true;
                startTime = System.currentTimeMillis(); // возвращаем количество миллисекунд 
            }
        }

        // Обновление таймера и проверка финиша
        if (isTimerRunning) { // если таймер запущен
            long elapsedTime = System.currentTimeMillis() - startTime; // Текущее время минус время старта = сколько миллисекунд прошло с начала движения
            
            // Отправляем время в окно
            if (timeListener != null) {
                timeListener.accept(elapsedTime);
            }

            // Проверяем финиш 
            java.awt.Shape finish = currentMap.getFinishZone(); // получаем финиш с текущей карты
            if (finish != null) {
                Rectangle2D robotHitbox = new Rectangle2D.Double(rx - 10, ry - 10, 20, 20);
                if (finish.intersects(robotHitbox)) {
                    isTimerRunning = false;
                    isFinished = true;
                    // Здесь в будущих задачах будет вызываться окно ввода имени
                }
            }
        }
    }
    
    // метод для пересчета экранных координат клика в виртуальные координаты мира
    private void onMouseClick(Point screenPoint) {
        double scale = Math.min((double) getWidth() / VIRTUAL_WIDTH, (double) getHeight() / VIRTUAL_HEIGHT);
        double offsetX = (getWidth() - VIRTUAL_WIDTH * scale) / 2;
        double offsetY = (getHeight() - VIRTUAL_HEIGHT * scale) / 2;

        // Переводим пиксели в метры виртуального мира
        int virtualX = (int) ((screenPoint.x - offsetX) / scale);
        int virtualY = (int) ((screenPoint.y - offsetY) / scale);

        // Обновляем цель (она теперь в виртуальных координатах)
        this.mouseTarget = new Point(virtualX, virtualY);
    }

    public void setMap(GameMap map) { // метод для смены карты извне
        this.currentMap = map;
        // Сброс позиции робота в начальную точку при смене карты 
        if (currentRobot != null && currentRobot.getBehavior() != null) {
            // левый нижний угол в виртуальном мире:
            double spawnX = 50;
            double spawnY = VIRTUAL_HEIGHT - 50; 
            
            // Передаем приказ роботу через интерфейс
            currentRobot.getBehavior().setPosition(spawnX, spawnY);
        }
        // Сброс таймера
        isTimerRunning = false;
        isFinished = false;
        if (timeListener != null) {
            timeListener.accept(0L); // Обнуляем текст в окне таймера
        }
        
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Вычисляем масштаб (выбираем минимальный, чтобы всё влезло и не исказилось)
        double scaleX = (double) getWidth() / VIRTUAL_WIDTH;
        double scaleY = (double) getHeight() / VIRTUAL_HEIGHT;
        double scale = Math.min(scaleX, scaleY);

        // вычисляем отступы, чтобы мир всегда был по центру окна
        double offsetX = (getWidth() - VIRTUAL_WIDTH * scale) / 2;
        double offsetY = (getHeight() - VIRTUAL_HEIGHT * scale) / 2;

        // запоминаем старую трансформацию и ставим новую
        AffineTransform oldTransform = g2d.getTransform(); // AffineTransform инструмент, который пересчитывает координаты
        
        g2d.translate(offsetX, offsetY); // Сдвигаем в центр
        g2d.scale(scale, scale); // Масштабируем

        // дальше все рисуем в виртуальных координатах
        
        if (currentMap != null) {
            currentMap.draw(g2d); // Карта рисуется в масштабе
        }

        if (mouseTarget != null) {
            g2d.setColor(Color.GREEN);
            g2d.fillOval(mouseTarget.x - 2, mouseTarget.y - 2, 5, 5);
        }
        
        if (currentRobot != null) { // отрисовка робота
            currentRobot.getVisualizer().draw(g2d, currentRobot.getBehavior());
        }

        // Возвращаем всё как было (важно для корректной работы Swing в будущем)
        g2d.setTransform(oldTransform);
    }
    

    public void stopAndResetTimer() { // останавливает игровой таймер и сбрасывает флаги 
        // Сбрасываем логические флаги таймера
        isTimerRunning = false;
        isFinished = false;
        startTime = 0;
        
        // Обнуляем текст в окне таймера (отправляем 0 миллисекунд)
        if (timeListener != null) {
            timeListener.accept(0L);
        }
        
        // Возвращаем робота на стартовую позицию (50, 750), 
        // чтобы при следующем открытии окон таймер не стартовал автоматически
        if (currentRobot != null && currentRobot.getBehavior() != null) {
            currentRobot.getBehavior().setPosition(50.0, VIRTUAL_HEIGHT - 50.0);
        }
        
        // Очищаем историю зажатых клавиш и кликов мыши, переводя игру в режим ожидания
        pressedKeys.clear();
        this.mouseTarget = null;
        
        // Запрашиваем перерисовку компонента
        repaint();
    }
}