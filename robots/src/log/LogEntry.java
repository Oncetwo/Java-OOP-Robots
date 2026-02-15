package log;

public class LogEntry // класс для записи логов (одна запись в логе)
{
    private LogLevel m_logLevel; // уровень важности
    private String m_strMessage; // текст сообщения
    
    public LogEntry(LogLevel logLevel, String strMessage)
    {
        m_strMessage = strMessage;
        m_logLevel = logLevel;
    }
    
    public String getMessage()
    {
        return m_strMessage;
    }
    
    public LogLevel getLevel()
    {
        return m_logLevel;
    }
}

