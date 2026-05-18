package gui;

import java.awt.BorderLayout; // компоновщик для расположения компонентов
import java.awt.Font; // для настройки шрифта
import java.util.ResourceBundle; // для локализации 
import javax.swing.JLabel; // текстовая метка
import javax.swing.JPanel; // панель-контейнер
import javax.swing.SwingConstants; // константы для выравнивания 
import localization.AbstractLocalizableWindow;

public class TimerWindow extends AbstractLocalizableWindow {
    private final JLabel timeLabel; // Текстовая метка для отображения времени

    public TimerWindow(ResourceBundle bundle) {
        super(bundle, "window.timer.title");
        
        timeLabel = new JLabel("00:00.000", SwingConstants.CENTER); 
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 36)); // моноширный Font.BOLD — жирный шрифт 36 пунктов
        
        JPanel panel = new JPanel(new BorderLayout()); //cоздаём панель с компоновщиком BorderLayout
        panel.add(timeLabel, BorderLayout.CENTER); // Помещаем метку в центр панели
        
        getContentPane().add(panel); // Добавляем панель в окно
        setSize(250, 120);
    }

    // Метод для обновления времени извне
    public void setTime(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        long ms = millis % 1000;
        timeLabel.setText(String.format("%02d:%02d.%03d", minutes, seconds, ms));
    }
}