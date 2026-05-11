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


public class RobotsProgram {
    public static void main(String[] args) {
        try {
            // Установка современного внешнего вида
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Предварительная настройка локали
        Locale systemLocale = Locale.getDefault();
        ResourceBundle bundle = ResourceBundle.getBundle("messages", systemLocale);
        Components.translateComponents(bundle);

        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame frame = new MainApplicationFrame(bundle);

            // Переменная для хранения профиля (пока не применяем)
            Profile profileToRestore = null;

            // 2. Логика выбора профиля
            List<String> profiles = ProfileManager.listProfiles();
            if (!profiles.isEmpty()) {
                int ask = JOptionPane.showConfirmDialog(
                        frame,
                        bundle.getString("dialog.profile.restore.exists"),
                        bundle.getString("dialog.confirm.title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (ask == JOptionPane.YES_OPTION) {
                    String chosen = null;
                    if (profiles.size() == 1) {
                        chosen = profiles.get(0);
                    } else {
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
                            // Загружаем объект из файла, но не вызываем restoreProfile сразу
                            profileToRestore = ProfileManager.loadProfile(chosen);
                        } catch (Exception ex) {
                            Logger.error("Failed to load profile: " + ex.getMessage());
                        }
                    }
                }
            }

            frame.pack();
            frame.setVisible(true);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH); // Теперь окно всегда будет большим при старте

            // 4. ТЕПЕРЬ применяем настройки из профиля
            if (profileToRestore != null) {
                // Профиль УЖЕ загружен, просто применяем его:
                frame.restoreProfile(profileToRestore);

                // РЕШЕНИЕ ПРОБЛЕМЫ С ЛОКАЛЬЮ И {0}:
                ResourceBundle currentBundle = frame.getBundle();
                String pattern = currentBundle.getString("log.message.profileRestored");

                // Используем profileToRestore.getName(), чтобы получить строку с именем!
                String finalMessage = java.text.MessageFormat.format(pattern, profileToRestore.getName());

                Logger.debug(finalMessage);

            } else {
                // Если профиля нет (первый запуск), только тогда разворачиваем на весь экран
                frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
            }
        });
    }
}