package gui;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
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
import api.IRobotPlugin;

import java.awt.event.WindowAdapter;

import log.Logger;
import localization.Localizable;

public class MainApplicationFrame extends JFrame // главное окно приложения (JFrame — главное окно)
{
    private final JDesktopPane desktopPane = new JDesktopPane(); // рабочая область (в которой будут внутренние окна)
    
    private ResourceBundle bundle; // текущий перевод (язык)
    
    private Locale currentLocale; // текущий выбранный язык (чтобы корректно отображать выбранный язык)
    
    private List<Localizable> localizableWindows = new ArrayList<>(); // Список всех окон, реализующих Localizable
    
    private LogWindow logWindow; // ссылки на окна
    private GameWindow gameWindow;
    private TimerWindow timerWindow;
    
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
        desktopPane.add(logWindow);
        logWindow.setVisible(true);
        localizableWindows.add(logWindow); // добавляем в список локализуемых окон

        gameWindow = new GameWindow(bundle);
        gameWindow.setSize(400,  400);
        desktopPane.add(gameWindow);
        gameWindow.setVisible(true);
        localizableWindows.add(gameWindow);
        
        // Создаем окно таймера
        timerWindow = new TimerWindow(bundle);
        timerWindow.setLocation(420, 10); 
        desktopPane.add(timerWindow);
        timerWindow.setVisible(true);
        localizableWindows.add(timerWindow);

        // Связываем визуализатор с окном таймера (паттерн Наблюдатель)
        gameWindow.getVisualizer().setTimeListener(timerWindow::setTime);

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

    private java.awt.Rectangle getRealWindowBounds(javax.swing.JInternalFrame window) {
        // Если окно развернуто или свернуто в значок, берем его "нормальные" сохраненные границы
        if (window.isMaximum() || window.isIcon()) {
            java.awt.Rectangle normalBounds = window.getNormalBounds();
            if (normalBounds != null) {
                return normalBounds;
            }
        }
        // Иначе возвращаем его текущие границы
        return window.getBounds();
    }

    private void switchLanguage(Locale newLocale) { // Метод для переключения языка
       
        ResourceBundle newBundle = ResourceBundle.getBundle("messages", newLocale); // Загружаем новый bundle
        
        Components.translateComponents(newBundle); // Обновляем стандартные компоненты Swing 
        this.bundle = newBundle; // Обновляем bundle в главном окне
        this.currentLocale = newLocale; // сохраняем выбранный язык
        
        for (Localizable window : localizableWindows) { // Обновляем все локализуемые окна 
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
        menuBar.add(createMapMenu(bundle)); 
        menuBar.add(createPluginMenu(bundle));
        menuBar.add(createWindowsMenu());

        return menuBar;
    }
    
    
     // Создает меню "Окна" для повторного открытия закрытых окон
    private JMenu createWindowsMenu() {
        // Безопасно получаем перевод (на случай, если ключи еще не добавлены в .properties просто берем русский вариант)
        String menuName = bundle.containsKey("menu.windows") ? bundle.getString("menu.windows") : "Окна";
        JMenu windowsMenu = new JMenu(menuName);
        windowsMenu.setMnemonic(KeyEvent.VK_W);

        String gameItemName = bundle.containsKey("menu.windows.showGame") ? bundle.getString("menu.windows.showGame") : "Показать игровое поле";
        JMenuItem gameItem = new JMenuItem(gameItemName);
        gameItem.addActionListener(e -> showGameWindow());
        windowsMenu.add(gameItem);

        String logItemName = bundle.containsKey("menu.windows.showLog") ? bundle.getString("menu.windows.showLog") : "Показать протокол работы";
        JMenuItem logItem = new JMenuItem(logItemName);
        logItem.addActionListener(e -> showLogWindow());
        windowsMenu.add(logItem);
        
        String timerItemName = bundle.containsKey("menu.windows.showTimer") ? bundle.getString("menu.windows.showTimer") : "Показать таймер";
        JMenuItem timerItem = new JMenuItem(timerItemName);
        timerItem.addActionListener(e -> showTimerWindow());
        windowsMenu.add(timerItem);

        return windowsMenu;
    }

     // Логика для показа Игрового поля
    private void showGameWindow() {
        // Если окна нет или оно было закрыто 
        if (gameWindow == null || gameWindow.isClosed()) {
            // удаляем старую ссылку из списка локализации, чтобы не было утечки памяти
            if (gameWindow != null) {
                localizableWindows.remove(gameWindow); 
            }
            
            // Создаем окно заново
            gameWindow = new GameWindow(bundle);
            gameWindow.setSize(400, 400);
            
            addWindow(gameWindow); 
            localizableWindows.add(gameWindow);
        } else {
            // Если окно просто свернуто (но не закрыто крестиком) - разворачиваем
            try {
                if (gameWindow.isIcon()) { // проверяет, свернуто ли окно в данный момент (минимизировано)
                    gameWindow.setIcon(false); // дает окну команду развернуться обратно
                }
                gameWindow.setSelected(true); // Выводим на передний план
            } catch (PropertyVetoException e) {
                Logger.error("Ошибка при развертывании игрового окна: " + e.getMessage());
            }
        }
    }

     // Логика для Протокола работы.
    private void showLogWindow() {
        if (logWindow == null || logWindow.isClosed()) {
            if (logWindow != null) {
                localizableWindows.remove(logWindow);
            }
            
            logWindow = createLogWindow();
            addWindow(logWindow);
            localizableWindows.add(logWindow);
        } else {
            try {
                if (logWindow.isIcon()) {
                    logWindow.setIcon(false);
                }
                logWindow.setSelected(true);
            } catch (PropertyVetoException e) {
                Logger.error("Ошибка при развертывании окна логов: " + e.getMessage());
            }
        }
    }
    
    // логика для окна таймера
    private void showTimerWindow() {
        if (timerWindow == null || timerWindow.isClosed()) {
            if (timerWindow != null) {
                localizableWindows.remove(timerWindow);
            }
            timerWindow = new TimerWindow(bundle);
            timerWindow.setLocation(420, 10);
            addWindow(timerWindow);
            localizableWindows.add(timerWindow);
            
            // переподключаем слушатель к новому объекту окна
            if (gameWindow != null && !gameWindow.isClosed()) {
                gameWindow.getVisualizer().setTimeListener(timerWindow::setTime);
            }
        } else {
            try {
                if (timerWindow.isIcon()) {
                	timerWindow.setIcon(false);
                }
                timerWindow.setSelected(true);
            } catch (PropertyVetoException e) {
                Logger.error("Ошибка при развертывании окна таймера: " + e.getMessage());
            }
        }
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

    public void saveCurrentProfile() {
        try {
            String profileName = "profile_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            List<WindowState> states = new ArrayList<>();

            if (logWindow != null) {
                java.awt.Rectangle bounds = getRealWindowBounds(logWindow);
                states.add(new WindowState(
                        "log",
                        bounds.x, bounds.y,
                        bounds.width, bounds.height,
                        logWindow.isVisible(),
                        logWindow.isIcon(),
                        logWindow.isMaximum()
                ));
            }

            // игра
            if (gameWindow != null) {
                java.awt.Rectangle bounds = getRealWindowBounds(gameWindow);
                states.add(new WindowState(
                        "game",
                        bounds.x, bounds.y,
                        bounds.width, bounds.height,
                        gameWindow.isVisible(),
                        gameWindow.isIcon(),
                        gameWindow.isMaximum()
                ));
            }

            Profile p = new Profile(profileName, currentLocale.getLanguage(), states);
            ProfileManager.saveProfile(p);
            Logger.debug(bundle.getString("log.message.profileSaved").replace("{0}", profileName));
        } catch (Exception ex) {
            // не ломаем выход приложения из-за ошибки сохранения профиля
            Logger.error("Failed to save profile: " + ex.getMessage());
        }
    }

    // Восстанавливает профиль в уже созданном окне
    public void restoreProfile(Profile profile) {
        if (profile == null) return;

        // Сначала восстановим локаль, если она отличается
        try {
            if (profile.getLocaleLanguage() != null && !profile.getLocaleLanguage().equals(currentLocale.getLanguage())) {
                switchLanguage(Locale.of(profile.getLocaleLanguage()));
            }
        } catch (Exception e) {
            //ignooooreeee
        }

        for (WindowState ws : profile.getWindows()) {
            try {
                if ("log".equals(ws.getId()) && logWindow != null) {
                    // Восстанавливаем строго в этом порядке!
                    logWindow.setBounds(ws.getX(), ws.getY(), ws.getWidth(), ws.getHeight());
                    logWindow.setVisible(ws.isVisible());
                    try { logWindow.setMaximum(ws.isMaximized()); } catch (PropertyVetoException ex) {}
                    try { logWindow.setIcon(ws.isIcon()); } catch (PropertyVetoException ex) {}

                } else if ("game".equals(ws.getId()) && gameWindow != null) {
                    // Восстанавливаем строго в этом порядке!
                    gameWindow.setBounds(ws.getX(), ws.getY(), ws.getWidth(), ws.getHeight());
                    gameWindow.setVisible(ws.isVisible());
                    try { gameWindow.setMaximum(ws.isMaximized()); } catch (PropertyVetoException ex) {}
                    try { gameWindow.setIcon(ws.isIcon()); } catch (PropertyVetoException ex) {}
                }
            } catch (Exception ex) {
                // не прерываем восстановление при ошибке частичного окна
            }
        }
    }

    private JMenu createPluginMenu(ResourceBundle bundle) {
        JMenu pluginMenu = new JMenu(bundle.getString("menu.plugins"));
        pluginMenu.setMnemonic(KeyEvent.VK_P);

        JMenuItem loadItem = new JMenuItem(bundle.getString("menu.plugins.load"));
        loadItem.addActionListener((ActionEvent e) -> {
            // Открываем диалоговое окно выбора файла
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(bundle.getString("dialog.plugin.chooser.title"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("JAR Files (*.jar)", "jar"));

            // Если пользователь выбрал файл и нажал "ОК"
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File jarFile = fileChooser.getSelectedFile();
                try {
                    // Загружаем плагин через наш лоадер
                    IRobotPlugin plugin = PluginLoader.loadPlugin(jarFile);

                    // Устанавливаем плагин в игровое окно (если оно открыто)
                    if (gameWindow != null) {
                        gameWindow.setRobotPlugin(plugin);
                        Logger.debug(bundle.getString("log.plugin.loaded") + " " + plugin.getName());
                    }
                } catch (Exception ex) {
                	Logger.error(bundle.getString("log.plugin.error") + " " + ex.getMessage());
                    JOptionPane.showMessageDialog(this, // создание стандартного окна с ошибкой
                            bundle.getString("dialog.plugin.error.message") + "\n" + ex.getMessage(),
                            bundle.getString("dialog.plugin.error.title"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pluginMenu.add(loadItem);
        return pluginMenu;
    }
    
    
    // Создание меню выбора карт
    private JMenu createMapMenu(ResourceBundle bundle) {
        JMenu mapMenu = new JMenu(bundle.getString("menu.maps"));
        mapMenu.setMnemonic(KeyEvent.VK_M);

        // Используем ButtonGroup, чтобы одновременно могла быть выбрана только одна карта
        ButtonGroup group = new ButtonGroup();

        // Создаем массив наших карт 
        api.GameMap[] maps = {
            new api.maps.EmptyMap(),
            new api.maps.CrossMap(),
            new api.maps.ArenaMap(),
            new api.maps.LabyrinthMap()
        };

        for (api.GameMap map : maps) {
        	String mapKey = map.getName(); // Получаем ключ от карты
        	
        	String mapDisplayName = bundle.containsKey(mapKey) ? bundle.getString(mapKey) : mapKey;
            
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(mapDisplayName);
            
            menuItem.addActionListener((event) -> { // при клике передаем выбранную карту в визуалайзер
                gameWindow.getVisualizer().setMap(map);
            });
            group.add(menuItem);
            mapMenu.add(menuItem);
            
            // По умолчанию выбираем первую карту (EmptyMap)
            if (map instanceof api.maps.EmptyMap) {
                menuItem.setSelected(true);
            }
        }

        return mapMenu;
    }

    public ResourceBundle getBundle() {
        return this.bundle;
    }
}