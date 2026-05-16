package gui;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;

public class ProfileManager {
    private static final File DIR = new File("profiles");

    static {
        if (!DIR.exists()) DIR.mkdirs();
    }

    public static List<String> listProfiles() {
        // получаем список файлов .xml
        String[] files = DIR.list((d, name) -> name.endsWith(".xml"));
        if (files == null) return Collections.emptyList();

        List<File> fileObjs = new ArrayList<>(); // создаём список файлов
        for (String f : files) fileObjs.add(new File(DIR, f)); // преобразуем имена файлов в объекты File

        // сортируем по времени - новые сверху
        fileObjs.sort(Comparator.comparingLong(File::lastModified).reversed());

        List<String> names = new ArrayList<>();
        for (File f : fileObjs) {
            String n = f.getName();
            // возвращаем только имя профиля (убираем расширение .xml)
            if (n.endsWith(".xml")) names.add(n.substring(0, n.length() - 4));
        }
        return names;
    }

    public static void saveProfile(Profile profile) throws IOException {
        String fname = profile.getName() + ".xml";
        File f = new File(DIR, fname);

        // Внутри saveProfile(Profile profile):
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(f)))) {
            encoder.writeObject(profile);
            encoder.flush(); // Принудительно выталкиваем данные в файл
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Profile loadProfile(String name) throws IOException {
        File f = new File(DIR, name + ".xml");

        // Внутри loadProfile(String name):
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(f)))) {
            return (Profile) decoder.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}