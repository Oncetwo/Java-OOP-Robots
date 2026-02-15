package log;

public final class Logger
{
    private static final LogWindowSource defaultLogSource; // поле класса, куда сохранять логи
    
    static { // статический инициализатор (выполняется 1 раз, когда класс загружается в память)
        defaultLogSource = new LogWindowSource(100); // (он создает объект LogWindowSource с размером очереди 100 записей)
    }
    
    private Logger() // приватный конструктор
    {
    }

    public static void debug(String strMessage) // метод доступен всем, вызывается без создания объекта
    {
        defaultLogSource.append(LogLevel.Debug, strMessage); // принимает строку - сообщение для лога
    }
    
    public static void error(String strMessage)
    {
        defaultLogSource.append(LogLevel.Error, strMessage);
    }

    public static LogWindowSource getDefaultLogSource() // геттер, позволяет другим классам получить ссылку на defaultLogSource
    {
        return defaultLogSource;
    }
}
