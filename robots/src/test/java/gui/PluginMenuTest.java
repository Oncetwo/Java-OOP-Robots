package gui;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import static org.junit.jupiter.api.Assertions.*;

public class PluginMenuTest {

    @Test
    void testPluginMenuStructureAndBindings() {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.of("ru"));
        MainApplicationFrame frame = new MainApplicationFrame(bundle);

        JMenuBar menuBar = frame.getJMenuBar();
        assertNotNull(menuBar, "Главное окно должно содержать панель меню");

        JMenu pluginMenu = null;
        String expectedMenuTitle = bundle.getString("menu.plugins");

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null && expectedMenuTitle.equals(menu.getText())) {
                pluginMenu = menu;
                break;
            }
        }

        assertNotNull(pluginMenu);
        assertEquals(KeyEvent.VK_P, pluginMenu.getMnemonic(), "Меню плагинов должно активироваться по кнопке P");
        assertTrue(pluginMenu.getItemCount() > 0, "Меню плагинов не должно быть пустым");

        JMenuItem loadItem = pluginMenu.getItem(pluginMenu.getItemCount() - 1);

        String expectedItemTitle = bundle.getString("menu.plugins.load");
        assertEquals(expectedItemTitle, loadItem.getText());
        assertTrue(loadItem.getActionListeners().length > 0, "Пункт меню загрузки должен иметь зарегистрированный ActionListener");
    }
}