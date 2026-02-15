package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JOptionPane;  // для диалогов подтверждения
import javax.swing.event.InternalFrameAdapter;  // для обработки закрытия окон
import javax.swing.event.InternalFrameEvent;  // событие закрытия
import java.awt.event.WindowAdapter;

import log.Logger;


public class MainApplicationFrame extends JFrame // главное окно приложения (JFrame — главное окно)
{
    private final JDesktopPane desktopPane = new JDesktopPane(); // рабочая область (в которой будут внутренние окна)
    
    public MainApplicationFrame() {
        int inset = 50; // окно с отступом 50 пикселей от каждого края экрана        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // возвращает ширину и длину экрана в пикселях
        setBounds(inset, inset, // устанавливаются координаты левого верхнего угла по горизонтали/диагонали и высота/ширина
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane); // метод JFrame, который заменяет содержимое окна
        
        
        LogWindow logWindow = createLogWindow();
        addWindowWithConfirmation(logWindow);  // добавляем окно с потдверждением закрытия

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindowWithConfirmation(gameWindow);  

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
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource()); // взяли источник логов
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
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
            "Закрыть окно \"" + frame.getTitle() + "\"?", // 2) текст сообщения
            "Подтверждение закрытия",
            JOptionPane.YES_NO_OPTION, // 3) тип кнопок
            JOptionPane.QUESTION_MESSAGE // 4) тип сообщения (появляется иконка вопроса)
        );
        
        if (result == JOptionPane.YES_OPTION) {
            frame.dispose(); // закрываем окно
            Logger.debug("Окно \"" + frame.getTitle() + "\" закрыто");
        }
    }
    
   
    private void confirmExitApplication() { // показывает диалог подтверждения выхода из приложения
        int result = JOptionPane.showConfirmDialog(
            this,
            "Вы действительно хотите выйти?",
            "Подтверждение выхода",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            Logger.debug("Приложение закрыто пользователем");
            System.exit(0);
        }
    }
    
    
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());    
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());

        return menuBar;
    }
    
    
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(createExitMenuItem()); // создаем пункт меню "выход"    
        return fileMenu;
    }
    
    
    protected JMenuItem createExitMenuItem() { // возвращаемый тип - объект пункта меню
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(event -> confirmExitApplication()); // слушатель действий
        return exitItem;
    }
    

    private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu("Режим отображения");
        menu.setMnemonic(KeyEvent.VK_V); //установили горячую клавишу
        menu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения"); //для людей с ограниченными возможностями

        menu.add(createSystemScheme());
        menu.add(createUniversalScheme());

        return menu;
    }

    private JMenuItem createSystemScheme() {
        JMenuItem item = new JMenuItem("Системная схема", KeyEvent.VK_S);
        item.addActionListener(event -> { //слушатель действий
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //смена на системный стиль
        });
        return item;
    }

    private JMenuItem createUniversalScheme() {
        JMenuItem item = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        item.addActionListener(event -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); //универсальный стиль
        });
        return item;
    }

    private JMenu createTestMenu() {
        JMenu menu = new JMenu("Тесты");
        menu.setMnemonic(KeyEvent.VK_T);
        menu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");
        menu.add(createLogMessage());

        return menu;
    }

    private JMenuItem createLogMessage() {
        JMenuItem item = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        item.addActionListener(event -> {
            Logger.debug("Новая строка"); //добавляем запись в лог
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
}