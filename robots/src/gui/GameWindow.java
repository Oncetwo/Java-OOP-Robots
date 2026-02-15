package gui;

import java.awt.BorderLayout;
import java.util.ResourceBundle;
import java.util.Locale;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;


public class GameWindow extends JInternalFrame implements Localizable
{
    private final GameVisualizer m_visualizer;

    private ResourceBundle bundle; // Храним bundle для обновления заголовка
    
    public GameWindow(ResourceBundle bundle) 
    {
        super(bundle.getString("window.game.title"), true, true, true, true); 
        this.bundle = bundle; 
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
    
    
    @Override
    public void updateLanguage(ResourceBundle newBundle) {
        this.bundle = newBundle;
        setTitle(bundle.getString("window.game.title")); // обновляем заголовок окна
    }
}