package gui;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardManager {
    private static final File FILE = new File("profiles/leaderboard.xml");

    public static void saveRecord(LeaderboardRecord record) {
        List<LeaderboardRecord> records = loadRecords();
        boolean foundExisting = false;

        for (int i = 0; i < records.size(); i++) {
            LeaderboardRecord existing = records.get(i);

            // Проверяем уникальное сочетание: Ник + Карта + Робот
            if (existing.getNickname().equals(record.getNickname()) &&
                    existing.getMapName().equals(record.getMapName()) &&
                    existing.getRobotName().equals(record.getRobotName())) {

                foundExisting = true;
                // Если новое время лучше (меньше), обновляем старый рекорд
                if (record.getTimeMs() < existing.getTimeMs()) {
                    records.set(i, record);
                }
                break; // Совпадение найдено, выходим из цикла
            }
        }

        // Если игрок еще ни разу не проходил эту карту на этом роботе, просто добавляем запись
        if (!foundExisting) {
            records.add(record);
        }

        // Сортируем таблицу (лучшие времена будут вверху)
        Collections.sort(records);

        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(FILE)))) {
            encoder.writeObject(records);
            encoder.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<LeaderboardRecord> loadRecords() {
        if (!FILE.exists()) return new ArrayList<>();

        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(FILE)))) {
            return (List<LeaderboardRecord>) decoder.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}