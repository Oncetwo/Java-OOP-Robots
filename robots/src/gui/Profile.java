package gui;

import java.io.Serializable;
import java.util.List;

public class Profile implements Serializable { // позволяет сохранять объект в файл
    private static final long serialVersionUID = 1L; // идентификатор версии

    private final String name; // имя профиля
    private final String localeLanguage; // язык локали
    private final List<WindowState> windows; // список состояний всех окон

    public Profile(String name, String localeLanguage, List<WindowState> windows) {
        this.name = name;
        this.localeLanguage = localeLanguage;
        this.windows = windows;
    }

    public String getName() { return name; }
    public String getLocaleLanguage() { return localeLanguage; }
    public List<WindowState> getWindows() { return windows; }
}