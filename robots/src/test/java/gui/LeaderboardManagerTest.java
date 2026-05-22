package gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LeaderboardManagerTest {

    private static final File FILE = new File("profiles/leaderboard.xml");

    @BeforeEach
    @AfterEach
    void cleanUp() {
        if (FILE.exists()) FILE.delete();
    }

    @Test
    void testSaveAndBestTimeLogic() {
        LeaderboardManager.saveRecord(new LeaderboardRecord("Player1", 20000, "map.cross", "DefaultRobot"));
        LeaderboardManager.saveRecord(new LeaderboardRecord("Player1", 25000, "map.cross", "DefaultRobot"));
        LeaderboardManager.saveRecord(new LeaderboardRecord("Player1", 15000, "map.cross", "DefaultRobot"));
        List<LeaderboardRecord> records = LeaderboardManager.loadRecords();
        assertEquals(1, records.size(), "Должна остаться только 1 уникальная запись для этой комбинации");
        assertEquals(15000, records.get(0).getTimeMs(), "Должно сохраниться самое лучшее (наименьшее) время");
        assertEquals("Player1", records.get(0).getNickname());
    }
}