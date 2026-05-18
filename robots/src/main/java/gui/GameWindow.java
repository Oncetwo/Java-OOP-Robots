package gui;

import java.awt.BorderLayout;
import java.util.ResourceBundle;
import javax.swing.JPanel;
import localization.AbstractLocalizableWindow;

public class GameWindow extends AbstractLocalizableWindow
{
    private final GameVisualizer m_visualizer;

    
    public GameWindow(ResourceBundle bundle) 
    {
    	super(bundle, "window.game.title");
        this.bundle = bundle; 
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }


    public void setRobotPlugin(api.IRobotPlugin plugin) {
        if (m_visualizer != null) {
            m_visualizer.setPlugin(plugin);
        }
    }
    
    // Метод для доступа к визуализатору извне (из мейн апликейшн)
    public GameVisualizer getVisualizer() {
        return m_visualizer; 
    }
    
    @Override
    public void dispose() {
        if (m_visualizer != null) { // перехватываем процесс уничтожения окна и перед тем, как оно исчезнет останаливаем таймер
            m_visualizer.stopAndResetTimer();
        }
        
        // Вызываем родительский метод для корректного освобождения ресурсов Swing
        super.dispose();
    }
}