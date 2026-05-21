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
            // установка внешнего вида
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Предварительная настройка локали
        Locale systemLocale = Locale.getDefault();
        ResourceBundle bundle = ResourceBundle.getBundle("messages", systemLocale);
        Components.translateComponents(bundle);

        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame frame = new MainApplicationFrame(bundle);

            // Переменная для хранения профиля
            Profile profileToRestore = null;

            // Логика выбора профиля
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
                            // Загружаем объект из файла
                            profileToRestore = ProfileManager.loadProfile(chosen);
                        } catch (Exception ex) {
                            Logger.error("Failed to load profile: " + ex.getMessage());
                        }
                    }
                }
            }
            String defaultNickname = (profileToRestore != null && profileToRestore.getNickname() != null)
                    ? profileToRestore.getNickname()
                    : "";

            // Вызываем окно ввода. Последний параметр defaultNickname подставит старый ник в текстовое поле
            String nickname = (String) JOptionPane.showInputDialog(
                    frame,
                    "Введите ваш никнейм для таблицы лидеров:",
                    "Выбор игрока",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    defaultNickname
            );

            // Если игрок закрыл окно или ввел пустую строку
            if (nickname == null || nickname.trim().isEmpty()) {
                // Если в профиле был ник — оставляем его, иначе генерируем случайный
                nickname = defaultNickname.isEmpty() ? "Player" + (int)(Math.random() * 1000) : defaultNickname;
            }

            // Передаем актуальный никнейм в главное окно
            frame.setCurrentNickname(nickname);

            frame.pack();
            frame.setVisible(true);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH); // Теперь окно всегда будет большим при старте

            if (profileToRestore != null) {
                frame.restoreProfile(profileToRestore);

                ResourceBundle currentBundle = frame.getBundle();
                String pattern = currentBundle.getString("log.message.profileRestored");

                // используем profileToRestore.getName(), чтобы получить строку с именем
                String finalMessage = java.text.MessageFormat.format(pattern, profileToRestore.getName());

                Logger.debug(finalMessage);

            } else {
                // Если профиля нет разворачиваем на весь экран
                frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
            }
        });
    }
}