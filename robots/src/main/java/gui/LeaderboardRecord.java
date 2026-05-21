package gui;

import java.io.Serializable;

public class LeaderboardRecord implements Serializable, Comparable<LeaderboardRecord> {
    private static final long serialVersionUID = 1L;

    private String nickname;
    private long timeMs;
    private String mapName;
    private String robotName;

    public LeaderboardRecord() {}

    public LeaderboardRecord(String nickname, long timeMs, String mapName, String robotName) {
        this.nickname = nickname;
        this.timeMs = timeMs;
        this.mapName = mapName;
        this.robotName = robotName;
    }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public long getTimeMs() { return timeMs; }
    public void setTimeMs(long timeMs) { this.timeMs = timeMs; }
    public String getMapName() { return mapName; }
    public void setMapName(String mapName) { this.mapName = mapName; }
    public String getRobotName() { return robotName; }
    public void setRobotName(String robotName) { this.robotName = robotName; }

    // Сортировка по времени (от меньшего к большему)
    @Override
    public int compareTo(LeaderboardRecord o) {
        return Long.compare(this.timeMs, o.timeMs);
    }
}