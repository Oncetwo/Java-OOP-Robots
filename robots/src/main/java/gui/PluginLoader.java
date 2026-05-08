package gui;

import api.IRobotPlugin;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

public class PluginLoader {

    public static IRobotPlugin loadPlugin(File jarFile) throws Exception {
        // Преобразуем путь к файлу в URL для ClassLoader
        URL jarUrl = jarFile.toURI().toURL();

        // Создаем загрузчик классов.
        // Важно: вторым параметром передаем текущий ClassLoader приложения,
        // чтобы плагин имел доступ к пакету api.*
        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{jarUrl},
                PluginLoader.class.getClassLoader()
        );

        // Ищем класс, реализующий интерфейс IRobotPlugin
        ServiceLoader<IRobotPlugin> loader = ServiceLoader.load(IRobotPlugin.class, classLoader);

        for (IRobotPlugin plugin : loader) {
            // Возвращаем первого найденного робота
            return plugin;
        }

        // Если цикл завершился, значит нужного класса нет
        throw new Exception("В JAR-файле не найдена реализация интерфейса api.IRobotPlugin.\n" +
                "Проверьте наличие файла META-INF/services/api.IRobotPlugin внутри JAR.");
    }
}