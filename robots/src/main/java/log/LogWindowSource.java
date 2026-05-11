package log;

import java.util.ArrayList;
import java.util.LinkedList;        
import java.util.List; 
import java.lang.ref.WeakReference;
import java.util.Iterator;

public class LogWindowSource
{
    private final int m_iQueueLength; // максимальное количество сообщений для хранения
    
    private final LinkedList<LogEntry> m_messages; // очередь сообщений лога (список)
    private final List<WeakReference<LogChangeListener>> m_listeners; // список слушателей (это слабая ссылка на объект, который реализует интерфейс LogChangeListener)
    private volatile LogChangeListener[] m_activeListeners; // кэш массива активных слушателей
    
    
    public LogWindowSource(int iQueueLength) 
    {
        m_iQueueLength = iQueueLength;
        m_messages = new LinkedList<>();
        m_listeners = new ArrayList<>();
    }
    
    
    public void registerListener(LogChangeListener listener) // метод регистрации слушателя
    {
        synchronized(m_listeners) // Блокировка для потокобезопасности (внутри критическая секция)
        {
            cleanupListeners(); // Очищаем мертвые ссылки перед добавлением новой
            
            m_listeners.add(new WeakReference<>(listener)); // Создает объект WeakReference, который хранит слабую ссылку на listener и помещает его в список
            
            m_activeListeners = null; // Сбрасываем кэш активных слушателей (т.е. при след уведомление создастся новый кэш)
        }
    }
    

    public void unregisterListener(LogChangeListener listener) // метод отписки слушателя
    {
        synchronized(m_listeners)
        {
            Iterator<WeakReference<LogChangeListener>> iterator = m_listeners.iterator(); // итератор для обхода списка
            while (iterator.hasNext()) // цикл по всем элементам списка
            {
                WeakReference<LogChangeListener> ref = iterator.next();
                LogChangeListener storedListener = ref.get(); // Пытаемся получить оригинаьный объект слушателя из слабой ссылки
                
                if (storedListener == null || storedListener == listener) // удаляем если слушатель равен переданному или объект был удален (ссылки нет)
                {
                    iterator.remove(); // удаляем этого слушателя
                }
            }
            m_activeListeners = null;
        }
    }
    
    
    private void cleanupListeners() // метод очистки ссылок, у которых объект удален (чтобы в списке слушателей не было пустых ссылок)
    {
        synchronized(m_listeners)
        {
            Iterator<WeakReference<LogChangeListener>> iterator = m_listeners.iterator();
            while (iterator.hasNext())
            {
                WeakReference<LogChangeListener> ref = iterator.next();
                if (ref.get() == null) // Объект уже удален сборщиком мусора
                {
                    iterator.remove(); // Удаляем пустую ссылку
                }
            }
        }
    }
    
    
    public void append(LogLevel logLevel, String strMessage) // добавление нового сообщения в лог
    {
        LogEntry entry = new LogEntry(logLevel, strMessage); // создаем объект сообщения лога
        
        synchronized(m_messages) // Синхронизируем доступ к очереди сообщений
        {
            m_messages.add(entry); // Добавляем новое сообщение в конец очереди
            
            if (m_messages.size() > m_iQueueLength) // если сообщений стало больше, чем лимит
            {
                m_messages.removeFirst();  // Удаляем самое старое сообщение
            }
        }
        
        LogChangeListener[] activeListeners = getActiveListeners(); // Получаем активных слушателей и уведомляем их
        for (LogChangeListener listener : activeListeners)
        {
            if (listener != null) // Проверяем, что слушатель есть
            {
                listener.onLogChanged();
            }
        }
    }
    
    
    private LogChangeListener[] getActiveListeners() // метод получения активных слушателей
    {
        LogChangeListener[] activeListeners = m_activeListeners; // считываем кэш в локальную переменную
        if (activeListeners == null)
        {
            synchronized (m_listeners) // Защита от одновременного создания кэша разными потоками
            {
                if (m_activeListeners == null)
                {
                    cleanupListeners(); // Очищаем пустые ссылки перед сбором активных
                    
                    List<LogChangeListener> liveListeners = new ArrayList<>(); // Временный список для сбора активных слушателей
                    
                    for (WeakReference<LogChangeListener> ref : m_listeners) // Проходим по всем WeakReference
                    {
                        LogChangeListener listener = ref.get();
                        if (listener != null)  // Только существующие слушатели
                        {
                            liveListeners.add(listener);
                        }
                    }
                    
                    activeListeners = liveListeners.toArray(new LogChangeListener[0]); // Преобразует список в массив
                    m_activeListeners = activeListeners; // сохраняем созданный массив в кэш
                }
            }
        }
        return activeListeners;
    }

    
    public int size()
    {
        synchronized(m_messages)
        {
            return m_messages.size();
        }
    }
    

    public Iterable<LogEntry> all()
    {
        synchronized(m_messages)
        {
            return new ArrayList<>(m_messages); // Возвращаем копию LinkedList
        }
    }
}
