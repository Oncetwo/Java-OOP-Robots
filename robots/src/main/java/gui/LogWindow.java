package gui;

import java.awt.BorderLayout;

import java.awt.EventQueue;
import javax.swing.JTextArea;
import java.util.ResourceBundle;
import javax.swing.JScrollPane;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import localization.AbstractLocalizableWindow;
import localization.Localizable;

public class LogWindow extends AbstractLocalizableWindow implements LogChangeListener
{
	// LogWindowSource - класс, который хранит сообщения лога и управляет слушателями (из пакета log)
    private final LogWindowSource m_logSource; // хранится ссылка на объект LogWindowSource
    
    // компонента Swing для отображения и редактирования многострочного текста (переменная хранит текстовое поле, в котором ПОКАЗЫВАЮТСЯ сообщения лога)
    private final JTextArea m_logContent;
    

    public LogWindow(LogWindowSource logSource, ResourceBundle bundle) 
    {
    	super(bundle, "window.log.title");
        this.bundle = bundle; 
        m_logSource = logSource; 
        
        m_logSource.registerListener(this); // Подписываемся на уведомления (registerListener() — метод, который добавляет слушателя)
        
        m_logContent = new JTextArea(""); // создаем тектовое поле
        m_logContent.setSize(200, 500); // устанавливаем размер поля
        m_logContent.setEditable(false);  // запрещаем редактирование
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(m_logContent), BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();// создаем панель с менеджером компановки, который делит область на 5 частей
        panel.add(m_logContent, BorderLayout.CENTER); // помещаем текстовое поле в центр
        getContentPane().add(panel); // добавляем панель в окно
        pack(); // эта штука вычисляет оптимальный размер окна на основе содержимого
        updateLogContent(); 
        
        // addInternalFrameListener() метод JInternalFrame для подписки на события (открытие, закрытие и тд)
        addInternalFrameListener(new InternalFrameAdapter() { // InternalFrameAdapter это адаптер, который реализует ВСЕ методы интерфейса пустышками
            @Override
            public void internalFrameClosing(InternalFrameEvent e) { // вызывается, когда пользователь начал закрывать окно
                m_logSource.unregisterListener(LogWindow.this); // описываемся
            }
        });
    }


    
    private void updateLogContent() // Обновление содержимого лога на экране (вызывается, когда приходят новые сообщения)
    {
        StringBuilder content = new StringBuilder();
        
        for (LogEntry entry : m_logSource.all()) // Получаем все сообщения из источника
        {
            content.append(entry.getMessage()).append("\n");
        }
        
        m_logContent.setText(content.toString()); // Устанавливаем текст в текстовое поле
        m_logContent.invalidate(); // Помечаем для перерисовки
    }
    
    @Override
    public void onLogChanged() // Метод интерфейса LogChangeListener
    {
        EventQueue.invokeLater(this::updateLogContent); // EventQueue.invokeLater() - просит систему выполнить код 
    }
}