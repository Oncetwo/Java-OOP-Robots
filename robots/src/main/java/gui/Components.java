package gui;

import java.util.ResourceBundle;
import javax.swing.UIManager;

/**
 * Класс отвечает за локализацию стандартных компонентов Swing:
 * - Кнопки в заголовках внутренних окон (свернуть, развернуть, закрыть)
 * - Тексты в системном меню окон
 * - Кнопки диалогов (Да, Нет, Отмена)
 */
public class Components {
    
    public static void translateComponents(ResourceBundle bundle) {
    	
        // Всплывающие подсказки при наведении на кнопки
        UIManager.put("InternalFrame.closeButtonToolTip", bundle.getString("swing.close"));
        UIManager.put("InternalFrame.maxButtonToolTip", bundle.getString("swing.maximize"));
        UIManager.put("InternalFrame.iconButtonToolTip", bundle.getString("swing.minimize"));
        
        // тесты в системном меню, появляются при правом клике на заголовок окна
        UIManager.put("InternalFrameTitlePane.restoreButtonText", bundle.getString("swing.restore"));
        UIManager.put("InternalFrameTitlePane.moveButtonText", bundle.getString("swing.move"));
        UIManager.put("InternalFrameTitlePane.sizeButtonText", bundle.getString("swing.size"));
        UIManager.put("InternalFrameTitlePane.minimizeButtonText", bundle.getString("swing.minimize"));
        UIManager.put("InternalFrameTitlePane.maximizeButtonText", bundle.getString("swing.maximize"));
        UIManager.put("InternalFrameTitlePane.closeButtonText", bundle.getString("swing.close"));
        
        // кнопки в диалоговых окнах
        UIManager.put("OptionPane.yesButtonText", bundle.getString("dialog.confirm.yes"));
        UIManager.put("OptionPane.noButtonText", bundle.getString("dialog.confirm.no"));
        UIManager.put("OptionPane.cancelButtonText", bundle.getString("dialog.confirm.no"));
    }
}