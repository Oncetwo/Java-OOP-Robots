package gui;

import api.IRobotPlugin;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RobotRegistry {

    // Хранилище: Имя Робота -> Метод его создания
    private static final Map<String, Supplier<IRobotPlugin>> registry = new LinkedHashMap<>();

    // Инициализация всех встроенных роботов
    static {
        register(() -> new api.DefaultRobot());
        register(() -> new api.ManualRobot());
        register(() -> new api.GhostRobot()); // Твой новый робот!
    }

    public static void register(Supplier<IRobotPlugin> robotFactory) {
        try {
            IRobotPlugin probe = robotFactory.get();
            registry.put(probe.getName(), robotFactory);
        } catch (Exception e) {
            log.Logger.error("Ошибка при регистрации робота: " + e.getMessage());
        }
    }

    public static List<String> getAvailableRobotNames() {
        return new ArrayList<>(registry.keySet());
    }

    public static IRobotPlugin createRobot(String name) {
        Supplier<IRobotPlugin> factory = registry.get(name);
        if (factory != null) {
            return factory.get();
        }
        log.Logger.error("Робот '" + name + "' не найден. Загружен стандартный.");
        return new api.DefaultRobot();
    }
}