package gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileManagerTest {

    private static final String TEST_PROFILE_NAME = "test_profile_junit";
    private File testFile;

    @BeforeEach
    void setUp() {
        testFile = new File("profiles", TEST_PROFILE_NAME + ".xml");
        if (testFile.exists()) testFile.delete();
    }

    @AfterEach
    void tearDown() {
        if (testFile.exists()) testFile.delete();
    }

    @Test
    public void testProfileSaveAndLoadWithMaximizedState() throws IOException {
        ArrayList<WindowState> windows = new ArrayList<>();

        WindowState gameWindow = new WindowState("game", 10, 20, 800, 600, true, false, false);
        WindowState logWindow = new WindowState("log", 0, 0, 300, 300, true, false, true);

        windows.add(gameWindow);
        windows.add(logWindow);

        Profile originalProfile = new Profile(TEST_PROFILE_NAME, "en", windows);

        ProfileManager.saveProfile(originalProfile);

        assertTrue(testFile.exists(), "XML файл профиля должен быть создан");
        assertTrue(testFile.length() > 0, "XML файл не должен быть пустым (0 байт)");

        Profile loadedProfile = ProfileManager.loadProfile(TEST_PROFILE_NAME);

        assertNotNull(loadedProfile, "Загруженный профиль не должен быть null");
        assertNotNull(loadedProfile.getWindows(), "Список окон не должен быть null (защита сработала)");

        assertEquals("en", loadedProfile.getLocaleLanguage(), "Язык должен сохраниться");
        assertEquals(2, loadedProfile.getWindows().size(), "Должно загрузиться ровно 2 окна");

        // Ищем окно логов и проверяем его масштаб
        WindowState loadedLogWindow = loadedProfile.getWindows().stream()
                .filter(w -> "log".equals(w.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(loadedLogWindow);
        assertTrue(loadedLogWindow.isMaximized(), "Окно логов должно запомнить масштабируемость");
        assertEquals(300, loadedLogWindow.getWidth(), "Ширина должна сохраниться");
    }
}