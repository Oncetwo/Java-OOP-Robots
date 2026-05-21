package gui;

import api.IRobotPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import static org.junit.jupiter.api.Assertions.*;

public class PluginLoaderTest {

    private File serviceFile = null;
    private File backupFile = null;

    @BeforeEach
    void disableParentService() {
        // находим, где в скомпилированном проекте лежит конфигурация
        URL url = PluginLoader.class.getClassLoader().getResource("META-INF/services/api.IRobotPlugin");
        if (url != null && "file".equals(url.getProtocol())) {
            try {
                serviceFile = new File(url.toURI());
                if (serviceFile.exists()) {
                    backupFile = new File(serviceFile.getParentFile(), "api.IRobotPlugin.bak");
                    serviceFile.renameTo(backupFile);
                }
            } catch (Exception e) {
                System.err.println();
            }
        }
    }

    @AfterEach
    void restoreParentService() {
        if (backupFile != null && backupFile.exists() && serviceFile != null) {
            backupFile.renameTo(serviceFile);
        }
    }

    @Test
    void testLoadValidPlugin() throws Exception {
        File testJar = File.createTempFile("mock_robot_plugin", ".jar");
        testJar.deleteOnExit();

        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(testJar))) {
            // пишем конфигурацию сервиса
            String servicePath = "META-INF/services/api.IRobotPlugin";
            jos.putNextEntry(new JarEntry(servicePath));
            jos.write((MockRobotPlugin.class.getName() + "\n").getBytes("UTF-8"));
            jos.closeEntry();
            String classAsPath = MockRobotPlugin.class.getName().replace('.', '/') + ".class";
            jos.putNextEntry(new JarEntry(classAsPath));
            jos.closeEntry();
        }

        IRobotPlugin loadedPlugin = PluginLoader.loadPlugin(testJar);

        assertNotNull(loadedPlugin, "Плагин должен успешно загрузиться");
        assertEquals("Тестовый Робот", loadedPlugin.getName(), "Имя робота должно совпадать с заданным в плагине");
        assertNotNull(loadedPlugin.getBehavior(), "Характеристики поведения должны быть заданы");
        assertNotNull(loadedPlugin.getVisualizer(), "Особенности визуализации должны быть заданы");
    }

    @Test
    void testLoadInvalidPlugin() throws IOException {
        File fakeJar = File.createTempFile("invalid_plugin", ".jar");
        fakeJar.deleteOnExit();
        Exception exception = assertThrows(Exception.class, () -> {
            PluginLoader.loadPlugin(fakeJar);
        });

        assertTrue(exception.getMessage().contains("В JAR-файле не найдена реализация интерфейса"));
    }
}