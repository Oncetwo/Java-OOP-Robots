package gui;

import api.GameContext;
import api.IRobotPlugin;
import api.DefaultRobot;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    // Храним активный плагин робота (по умолчанию загружаем нашего стандартного)
    private IRobotPlugin currentRobot = new DefaultRobot();

    // Координаты цели (мышки)
    private Point mouseTarget = new Point(150, 100);

    public GameVisualizer() {
        m_timer.schedule(new TimerTask() { //инициирует перерисовку
            @Override
            public void run() { EventQueue.invokeLater(GameVisualizer.this::repaint); }
        }, 0, 50);

        m_timer.schedule(new TimerTask() { //каждый 10мс вызывать обновление логики движения
            @Override
            public void run() { onModelUpdateEvent(); }
        }, 0, 10);

        addMouseListener(new MouseAdapter() { //следит за мышкой
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseTarget = e.getPoint();
                repaint(); // Перерисовываем сразу после клика
            }
        });

        setDoubleBuffered(true);
    }

    // Метод для загрузки нового робота извне
    public void setPlugin(IRobotPlugin plugin) {
        if (plugin != null) {
            this.currentRobot = plugin;
        }
    }

    // Обновление логики (Модели)
    protected void onModelUpdateEvent() {
        if (currentRobot == null || currentRobot.getBehavior() == null) return;

        // Создаем контекст: "фотографию" текущего состояния игры
        GameContext context = new GameContext() {
            @Override public Point getMouseTarget() { return mouseTarget; }
            @Override public Set<Integer> getPressedKeys() { return Collections.emptySet(); } // Заглушка для ручного управления (п.5)
            @Override public int getFieldWidth() { return getWidth(); }
            @Override public int getFieldHeight() { return getHeight(); }
            @Override public double getDeltaTime() { return 10.0; } // 10 мс из таймера
        };

        // Отдаем контекст "мозгу" робота
        currentRobot.getBehavior().update(context);
    }

    // Обновление графики (Представления)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Сбрасываем трансформацию перед отрисовкой карты и цели
        g2d.setTransform(new AffineTransform());

        // 2. Рисуем цель (мышь)
        g2d.setColor(Color.GREEN);
        g2d.fillOval(mouseTarget.x - 2, mouseTarget.y - 2, 5, 5);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(mouseTarget.x - 2, mouseTarget.y - 2, 5, 5);

        // 3. отдаём работу визуализатору
        if (currentRobot != null && currentRobot.getVisualizer() != null && currentRobot.getBehavior() != null) {
            currentRobot.getVisualizer().draw(g2d, currentRobot.getBehavior());
        }
    }
}