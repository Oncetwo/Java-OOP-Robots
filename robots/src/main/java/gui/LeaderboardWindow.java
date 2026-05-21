package gui;

import localization.AbstractLocalizableWindow;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

public class LeaderboardWindow extends AbstractLocalizableWindow {

    private final JTable table;
    private final DefaultTableModel tableModel;

    public LeaderboardWindow(ResourceBundle bundle) {
        // Предполагается, что в messages.properties будет ключ window.leaderboard.title=Таблица лидеров
        super(bundle, "window.leaderboard.title");

        String[] columnNames = {"Ранг", "Никнейм", "Время", "Карта", "Робот"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Запрет редактирования
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setSize(500, 300);
        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0); // Очистка таблицы
        List<LeaderboardRecord> records = LeaderboardManager.loadRecords();
        int rank = 1;
        for (LeaderboardRecord record : records) {
            String timeStr = String.format("%02d:%02d.%03d",
                    (record.getTimeMs() / 1000) / 60,
                    (record.getTimeMs() / 1000) % 60,
                    record.getTimeMs() % 1000);

            tableModel.addRow(new Object[]{
                    rank++,
                    record.getNickname(),
                    timeStr,
                    record.getMapName(),
                    record.getRobotName()
            });
        }
    }
}