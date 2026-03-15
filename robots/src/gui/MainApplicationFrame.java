package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import java.beans.PropertyVetoException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import gui.Profile;
import gui.WindowState;
import gui.ProfileManager;
import javax.swing.ButtonGroup;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import java.awt.event.WindowAdapter;

import log.Logger;

public class MainApplicationFrame extends JFrame // главное окно приложения (JFrame — главное окно)
{
    private final JDesktopPane desktopPane = new JDesktopPane(); // рабочая область (в которой будут внутренние окна)
    
    private ResourceBundle bundle; // текущий перевод (язык)
    
    private Locale currentLocale; // текущий выбранный язык (чтобы корректно отображать выбранный язык)
    
    private List<Localizable> localizableWindows = new ArrayList<>(); // Список всех окон, реализующих Localizable
    
    private LogWindow logWindow; // ссылки на окна
    private GameWindow gameWindow;
    
    public MainApplicationFrame(ResourceBundle bundle) {
        this.bundle = bundle; 
        this.currentLocale = bundle.getLocale(); // сохраняем текущий язык
        
        int inset = 50; // окно с отступом 50 пикселей от каждого края экрана        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // возвращает ширину и длину экрана в пикселях
        setBounds(inset, inset, // устанавливаются координаты левого верхнего угла по горизонтали/диагонали и высота/ширина
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane); // метод JFrame, который заменяет содержимое окна
        
        
        logWindow = createLogWindow();
        addWindowWithConfirmation(logWindow);
        localizableWindows.add(logWindow); // добавляем в список локализуемых окон

        gameWindow = new GameWindow(bundle);
        gameWindow.setSize(400,  400);
        addWindowWithConfirmation(gameWindow);
        localizableWindows.add(gameWindow);

        setJMenuBar(generateMenuBar()); // метод JFrame для установки меню
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);  // отключаем автоматическое закрытие при нажатие на крестик
        
        addWindowListener(new WindowAdapter() { // добавляем слушатель для подтверждения выхода из приложения
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmExitApplication(); // метод, который показывает диалог подтверждения
            }
        });
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource(), bundle); // взяли источник логов
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(bundle.getString("log.message.works")); // используем ключ для "Протокол работает"
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame) // параметр: любое внутреннее окно
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
// 
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
// 
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        return menuBar;
//    }

    
    protected void addWindowWithConfirmation(JInternalFrame frame) { // метод добавляет окно с подтверждением закрытия
        frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE); // Отключаем стандартное закрытие
        
        // Добавляем слушатель, который перехватывает событие закрытия
        frame.addInternalFrameListener(new InternalFrameAdapter() { // InternalFrameAdapter — аналог WindowAdapter, но для внутренних окон
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                confirmCloseFrame(frame);  // показываем диалог подтверждения
            }
        });
        
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
   
    private void confirmCloseFrame(JInternalFrame frame) {// показывает диалог подтверждения закрытия отдельного окна
        int result = JOptionPane.showConfirmDialog( // статический метод, который показывает стандартный диалог с вопросом
            this, // 1) ссылка на текущее главное окно (диалог в центре главного окна)
            bundle.getString("dialog.confirm.close").replace("{0}", frame.getTitle()), // ключ для "Закрыть окно"
            bundle.getString("dialog.confirm.title"), // ключ названия окна
            JOptionPane.YES_NO_OPTION, // 3) тип кнопок
            JOptionPane.QUESTION_MESSAGE // 4) тип сообщения (появляется иконка вопроса)
        );
        
        if (result == JOptionPane.YES_OPTION) {
            frame.dispose(); // закрываем окно
            Logger.debug(bundle.getString("log.message.windowClosed") 
                .replace("{0}", frame.getTitle())); // ключ для "Окно "название" закрыто"
        }
    }
    
   
    private void confirmExitApplication() { // показывает диалог подтверждения выхода из приложения
        int result = JOptionPane.showConfirmDialog(
            this,
            bundle.getString("dialog.confirm.exit"), 
            bundle.getString("dialog.confirm.title"), 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            Logger.debug(bundle.getString("log.message.appClosed"));
            saveCurrentProfile();
            System.exit(0);
        }
    }
    
   
    private void switchLanguage(Locale newLocale) { // Метод для переключения языка
       
        ResourceBundle newBundle = ResourceBundle.getBundle("messages", newLocale); // Загружаем новый bundle
        
        Components.translateComponents(newBundle); // Обновляем стандартные компоненты Swing 
        this.bundle = newBundle; // Обновляем bundle в главном окне
        this.currentLocale = newLocale; // сохраняем выбранный язык
        
        for (Localizable window : localizableWindows) { // Обновляем все локализуемые окна через интерфейс
            window.updateLanguage(newBundle);
        }
        
        setJMenuBar(generateMenuBar()); // Обновляем меню (пересоздаём с новыми переводами)     
        SwingUtilities.updateComponentTreeUI(this); // Перерисовываем окно
    }
    
    
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());    
        menuBar.add(createLanguageMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());

        return menuBar;
    }
    
    
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu(bundle.getString("menu.file")); 
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(createExitMenuItem()); // создаем пункт меню "выход"    
        return fileMenu;
    }
    
    
    protected JMenuItem createExitMenuItem() { // возвращаемый тип - объект пункта меню
        JMenuItem exitItem = new JMenuItem(bundle.getString("menu.file.exit")); 
        exitItem.addActionListener(event -> confirmExitApplication()); // слушатель действий
        return exitItem;
    }
    
    
    private JMenu createLanguageMenu() { // создание меню для выбора языка
    	
        JMenu languageMenu = new JMenu(bundle.getString("menu.language")); 
        languageMenu.setMnemonic(KeyEvent.VK_L);
        
        ButtonGroup group = new ButtonGroup(); // логическая группа
        
        // Русский язык
        JRadioButtonMenuItem ruItem = new JRadioButtonMenuItem(bundle.getString("menu.language.ru")); // пункт меню "русский"
        ruItem.setSelected(currentLocale.getLanguage().equals("ru")); // если выбран русский, то этот пункт будет отмечен
        ruItem.addActionListener(e -> switchLanguage(Locale.of("ru"))); // при нажатие меняем язык
        group.add(ruItem); // добавляем в группу
        languageMenu.add(ruItem); // добавляем в меню
        
        // Английский язык
        JRadioButtonMenuItem enItem = new JRadioButtonMenuItem(bundle.getString("menu.language.en"));
        enItem.setSelected(currentLocale.getLanguage().equals("en")); // если выбран английский, то этот пункт будет отмечен
        enItem.addActionListener(e -> switchLanguage(Locale.of("en")));
        group.add(enItem);
        languageMenu.add(enItem);
        
        return languageMenu;
    }
    

    private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu(bundle.getString("menu.view.lookAndFeel")); 
        menu.setMnemonic(KeyEvent.VK_V); //установили горячую клавишу
        menu.getAccessibleContext().setAccessibleDescription(
                bundle.getString("menu.view.lookAndFeel")); 

        menu.add(createSystemScheme());
        menu.add(createUniversalScheme());

        return menu;
    }

    private JMenuItem createSystemScheme() {
        JMenuItem item = new JMenuItem(bundle.getString("menu.view.system"), KeyEvent.VK_S); 
        item.addActionListener(event -> { //слушатель действий
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //смена на системный стиль
        });
        return item;
    }

    private JMenuItem createUniversalScheme() {
        JMenuItem item = new JMenuItem(bundle.getString("menu.view.universal"), KeyEvent.VK_S); 
        item.addActionListener(event -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); //универсальный стиль
        });
        return item;
    }

    private JMenu createTestMenu() {
        JMenu menu = new JMenu(bundle.getString("menu.test")); 
        menu.setMnemonic(KeyEvent.VK_T);
        menu.getAccessibleContext().setAccessibleDescription(
                bundle.getString("menu.test")); 
        menu.add(createLogMessage());

        return menu;
    }

    private JMenuItem createLogMessage() {
        JMenuItem item = new JMenuItem(bundle.getString("menu.test.logMessage"), KeyEvent.VK_S); 
        item.addActionListener(event -> {
            Logger.debug(bundle.getString("log.message.newLine")); 
        });
        return item;
    }
    
    private void setLookAndFeel(String className) // метод для изменения темы приложения, на вход полное имя класса, который хотим применять
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this); // реккурсивно обновляем вид компонентов
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    // Сохраняет текущий профиль (называем профиль timestamp'ом)
    public void saveCurrentProfile() {
        try { // создаём имя профиля по времени
            String profileName = "profile_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            List<WindowState> states = new ArrayList<>();

            // лог
            if (logWindow != null) {
                states.add(new WindowState(
                        "log",
                        logWindow.getX(), logWindow.getY(),
                        logWindow.getWidth(), logWindow.getHeight(),
                        logWindow.isVisible(),
                        logWindow.isIcon(),
                        logWindow.isMaximum()
                ));
            }

            // игра
            if (gameWindow != null) {
                states.add(new WindowState(
                        "game",
                        gameWindow.getX(), gameWindow.getY(),
                        gameWindow.getWidth(), gameWindow.getHeight(),
                        gameWindow.isVisible(),
                        gameWindow.isIcon(),
                        gameWindow.isMaximum()
                ));
            }

            Profile p = new Profile(profileName, currentLocale.getLanguage(), states); // создали профиль
            ProfileManager.saveProfile(p);
            Logger.debug(bundle.getString("log.message.profileSaved").replace("{0}", profileName));
        } catch (Exception ex) {
            // не ломаем выход приложения из-за ошибки сохранения профиля
            Logger.error("Failed to save profile: " + ex.getMessage());
        }
    }

    // Восстанавливает профиль в уже созданном окне
    public void restoreProfile(Profile p) {
        if (p == null) return;

        // Сначала восстановим локаль, если она отличается
        try {
            if (p.getLocaleLanguage() != null && !p.getLocaleLanguage().equals(currentLocale.getLanguage())) {
                switchLanguage(Locale.of(p.getLocaleLanguage())); // switchLanguage приватный — доступен внутри класса
            }
        } catch (Exception e) {
            // игнорируем неверные локали
        }

        for (WindowState ws : p.getWindows()) {
            try {
                if ("log".equals(ws.getId()) && logWindow != null) { // восстанавливаем профиль
                    logWindow.setLocation(ws.getX(), ws.getY());
                    logWindow.setSize(ws.getWidth(), ws.getHeight());
                    logWindow.setVisible(ws.isVisible());
                    try { logWindow.setIcon(ws.isIcon()); } catch (PropertyVetoException ex) {}
                    try { logWindow.setMaximum(ws.isMaximized()); } catch (PropertyVetoException ex) {}
                } else if ("game".equals(ws.getId()) && gameWindow != null) {
                    gameWindow.setLocation(ws.getX(), ws.getY());
                    gameWindow.setSize(ws.getWidth(), ws.getHeight());
                    gameWindow.setVisible(ws.isVisible());
                    try { gameWindow.setIcon(ws.isIcon()); } catch (PropertyVetoException ex) {}
                    try { gameWindow.setMaximum(ws.isMaximized()); } catch (PropertyVetoException ex) {}
                }
            } catch (Exception ex) {
                // не прерываем восстановление при ошибке частичного окна
            }
        }
    }
}