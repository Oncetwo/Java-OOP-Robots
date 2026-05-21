package gui;

import java.io.Serializable;
import java.util.List;

public class Profile implements Serializable { // позволяет сохранять объект в файл
    private static final long serialVersionUID = 1L; // идентификатор версии

    private String name; // имя профиля
    private String localeLanguage; // язык локали
    private List<WindowState> windows; // список состояний всех окон
    private String nickname;
    private String mapName;
    private String robotName;
    private double robotX;
    private double robotY;
    private long savedTime;
    public Profile() {}

    public Profile(String name, String localeLanguage, List<WindowState> windows) {
        this.name = name;
        this.localeLanguage = localeLanguage;
        this.windows = windows;
    }
    public void setWindows(java.util.ArrayList<WindowState> windows) {
        this.windows = windows;
    }

    public String getName() { return name; }
    public String getLocaleLanguage() { return localeLanguage; }
    public List<WindowState> getWindows() {
        if (this.windows == null) {
            this.windows = new java.util.ArrayList<>();
        }
        return this.windows;
    }
    public void setName(String name) { this.name = name; }
    public void setLocaleLanguage(String localeLanguage) { this.localeLanguage = localeLanguage; }
    public void setWindows(List<WindowState> windows) { this.windows = windows; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getMapName() { return mapName; }
    public void setMapName(String mapName) { this.mapName = mapName; }

    public String getRobotName() { return robotName; }
    public void setRobotName(String robotName) { this.robotName = robotName; }

    public double getRobotX() { return robotX; }
    public void setRobotX(double robotX) { this.robotX = robotX; }

    public double getRobotY() { return robotY; }
    public void setRobotY(double robotY) { this.robotY = robotY; }

    public long getSavedTime() { return savedTime; }
    public void setSavedTime(long savedTime) { this.savedTime = savedTime; }
}