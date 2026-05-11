package gui;

import java.io.Serializable;
import java.util.List;

public class Profile implements Serializable { // позволяет сохранять объект в файл
    private static final long serialVersionUID = 1L; // идентификатор версии

    private String name; // имя профиля
    private String localeLanguage; // язык локали
    private List<WindowState> windows; // список состояний всех окон
    public Profile() {}

    public Profile(String name, String localeLanguage, List<WindowState> windows) {
        this.name = name;
        this.localeLanguage = localeLanguage;
        this.windows = windows;
    }

    public String getName() { return name; }
    public String getLocaleLanguage() { return localeLanguage; }
    public List<WindowState> getWindows() { return windows; }
    public void setName(String name) { this.name = name; }
    public void setLocaleLanguage(String localeLanguage) { this.localeLanguage = localeLanguage; }
    public void setWindows(List<WindowState> windows) { this.windows = windows; }
}