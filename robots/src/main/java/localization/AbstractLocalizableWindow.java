package localization;


import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import java.util.ResourceBundle;

// Базовый класс для всех локализуемых окон приложения

public abstract class AbstractLocalizableWindow extends JInternalFrame implements Localizable {
    protected ResourceBundle bundle;  // Храним bundle для обновления заголовка
    private final String titleKey; // запоминаем ключ заголовка 

    public AbstractLocalizableWindow(ResourceBundle bundle, String titleKey) {
        // Устанавливаем параметры окон по умолчанию
        super(bundle.getString(titleKey), true, true, true, true);
        this.bundle = bundle;
        this.titleKey = titleKey;
        
        // запрещаем автоматическое закрытие 
        setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

        // добавляем слушателя, который перехватит нажатие на крестик
        addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
                confirmClose(); // Вызываем метод с вопросом
            }
        });
    }

    
    @Override
    public void updateLanguage(ResourceBundle newBundle) {
        this.bundle = newBundle;
        setTitle(bundle.getString(titleKey)); // обновляем заголовок окна
    }
    
    private void confirmClose() {
        int result = JOptionPane.showConfirmDialog(
            this,
            bundle.getString("dialog.confirm.close").replace("{0}", getTitle()),
            bundle.getString("dialog.confirm.title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            dispose(); // Окно закрывается само
        }
    }
}