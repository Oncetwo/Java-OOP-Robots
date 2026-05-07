package gui;

import org.junit.jupiter.api.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileManagerTest {

    private static File profileDir;

    @BeforeAll
    public static void setup() {
        profileDir = new File(System.getProperty("user.home"), ".robots_profiles");
        if (!profileDir.exists()) {
            profileDir.mkdirs();
        }
    }

    @AfterEach
    public void cleanup() {
        // удаляем тестовые профили, которые могли быть созданы тестами
        File[] files = profileDir.listFiles((dir, name) ->
                name.startsWith("testprofile") || name.startsWith("testprofile2"));
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    @Test
    public void testSaveAndLoadProfile() throws Exception {
        WindowState ws = new WindowState("log", 10, 20, 100, 200, true, false, false);
        Profile profile = new Profile("testprofile", "en", Arrays.asList(ws));

        ProfileManager.saveProfile(profile);

        List<String> names = ProfileManager.listProfiles();
        assertTrue(names.contains("testprofile"), "Список профилей должен содержать сохранённый профиль");

        Profile loaded = ProfileManager.loadProfile("testprofile");

        assertNotNull(loaded);
        assertEquals("testprofile", loaded.getName());
        assertEquals("en", loaded.getLocaleLanguage());
        assertEquals(1, loaded.getWindows().size());

        WindowState loadedWs = loaded.getWindows().get(0);
        assertEquals("log", loadedWs.getId());
        assertEquals(10, loadedWs.getX());
        assertEquals(20, loadedWs.getY());
    }

    @Test
    public void testListProfilesContainsSaved() throws Exception {
        Profile p = new Profile("testprofile2", "ru", Arrays.asList());
        ProfileManager.saveProfile(p);

        List<String> names = ProfileManager.listProfiles();
        assertTrue(names.contains("testprofile2"), "Список профилей должен содержать testprofile2");
    }

    @Test
    public void testLoadNonexistentProfileThrows() {
        assertThrows(Exception.class, () -> {
            ProfileManager.loadProfile("not_existing_profile");
        });
    }
}