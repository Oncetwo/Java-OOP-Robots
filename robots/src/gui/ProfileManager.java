package gui;

import java.io.*;
import java.util.*;

public class ProfileManager {
    private static final File DIR = new File(System.getProperty("user.home"), ".robots_profiles"); // создаём папку

    static {
        if (!DIR.exists()) DIR.mkdirs(); // если папка не создана - создаётся
    }

    // список имён профилей (файлы *.ser без расширения), отсортирован от новых к старым
    public static List<String> listProfiles() {
        String[] files = DIR.list((d, name) -> name.endsWith(".ser")); // получаем список файлов .ser
        if (files == null) return Collections.emptyList();

        List<File> fileObjs = new ArrayList<>(); // создаём список файлов
        for (String f : files) fileObjs.add(new File(DIR, f)); // преобразуем имена файлов в объекты File
        fileObjs.sort(Comparator.comparingLong(File::lastModified).reversed()); // сортируем по времени - новые сверху
        List<String> names = new ArrayList<>();
        for (File f : fileObjs) {
            String n = f.getName(); // возвращаем только имя профиля
            if (n.endsWith(".ser")) names.add(n.substring(0, n.length() - 4));
        }
        return names;
    }

    public static void saveProfile(Profile profile) throws IOException {
        String fname = profile.getName() + ".ser"; // формируем имя
        File f = new File(DIR, fname);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) { // создаём поток записи объекта в файл
            oos.writeObject(profile); // сохранили объект profile в файл
        }
    }

    public static Profile loadProfile(String name) throws IOException, ClassNotFoundException {
        File f = new File(DIR, name + ".ser");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) { // читаем объект
            Object o = ois.readObject(); // преобразуя в тип Profile
            return (Profile) o;
        }
    }
}