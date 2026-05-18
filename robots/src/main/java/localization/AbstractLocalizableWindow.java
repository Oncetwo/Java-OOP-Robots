package localization;


import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import java.util.ResourceBundle;

// Базовый класс для всех локализуемых окон приложения

public abstract class AbstractLocalizableWindow extends JInternalFrame implements Localizable {
    protected ResourceBundle bundle;  // Храним bundle для обновления заголовка
    private final String titleKey; // запоминаем ключ заголовка 
    
    private boolean isClosingProgrammatically = false; // флаг для защиты от бесконечной рекурсии

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
    	
    	if (isClosingProgrammatically) {
            return;
        }
    	
        int result = JOptionPane.showConfirmDialog(
            this,
            bundle.getString("dialog.confirm.close").replace("{0}", getTitle()),
            bundle.getString("dialog.confirm.title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
        	try {
        		isClosingProgrammatically = true;
                setClosed(true); //  переводим в состояние закрыто (флаг внутренного состояния поменяется на закрытое)
            } catch (java.beans.PropertyVetoException ex) {
            	isClosingProgrammatically = false;
                // Если система по какой-то причине заблокировала закрытие, используем dispose (освобождаем ресурсы экрана)
                dispose();
            }
        }
    }
}