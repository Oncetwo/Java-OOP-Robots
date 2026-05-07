package log;

public enum LogLevel
{ // константы, каждой приписано числовое значение (важность)
    Trace(0),
    Debug(1),
    Info(2),
    Warning(3),
    Error(4),
    Fatal(5);
    
    private int m_iLevel;
    
    private LogLevel(int iLevel) // конструктор
    {
        m_iLevel = iLevel;
    }
    
    public int level() // сеттер
    {
        return m_iLevel;
    }
}

