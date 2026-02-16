package gui;

import java.awt.Frame;
import java.util.Locale;
import java.util.ResourceBundle;


import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RobotsProgram
{
    public static void main(String[] args) {
      try {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      // Определяем язык системы 
      Locale systemLocale = Locale.getDefault();
      
      // Загружаем переводы для языка системы
      ResourceBundle bundle = ResourceBundle.getBundle("messages", systemLocale);
      
      // Переводим стандартные компоненты Swing
      Components.translateComponents(bundle);
      
      SwingUtilities.invokeLater(() -> {
        MainApplicationFrame frame = new MainApplicationFrame(bundle);
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
      });
    }}
