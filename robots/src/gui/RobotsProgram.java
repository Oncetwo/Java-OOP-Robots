package gui;

import java.awt.*;
import java.awt.Frame;
import java.util.Locale;
import java.util.ResourceBundle;

import log.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.util.List;

import javax.swing.JOptionPane;


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
            // Прежде чем показывать — проверим, есть ли сохранённые профили
            List<String> profiles = ProfileManager.listProfiles(); // получаем список сохранённых профилей
            if (!profiles.isEmpty()) {
                // Сначала спросим восстанавливать ли профиль (в текущей локали bundle)
                int ask = JOptionPane.showConfirmDialog(
                        frame,
                        bundle.getString("dialog.profile.restore.exists"), // например: "Найден сохранённый профиль. Восстановить?"
                        bundle.getString("dialog.confirm.title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (ask == JOptionPane.YES_OPTION) {
                    // Если несколько — предложим выбор
                    String chosen;
                    if (profiles.size() == 1) {
                        chosen = profiles.get(0);
                    } else {
                        // подпись в локали: bundle.getString("dialog.profile.choose")
                        Object sel = JOptionPane.showInputDialog(
                                frame,
                                bundle.getString("dialog.profile.choose"),
                                bundle.getString("dialog.profile.choose.title"),
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                profiles.toArray(new String[0]),
                                profiles.get(0)
                        );
                        chosen = (sel instanceof String) ? (String) sel : null;
                    }
                    if (chosen != null) {
                        try {
                            Profile p = ProfileManager.loadProfile(chosen);
                            // восстановим профиль (внутри него есть локаль и состояния окон)
                            frame.restoreProfile(p);
                            Logger.debug(bundle.getString("log.message.profileRestored").replace("{0}", chosen));
                        } catch (Exception ex) {
                            Logger.error("Failed to load profile: " + ex.getMessage());
                        }
                    }
                }
            }

            frame.pack();
            frame.setVisible(true);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        });
    }}
