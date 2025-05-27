package org.example.project_rplbo.util;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NotificationUtil {

    public static void checkForDueTasks() {
        try (Connection conn = DBConnectionUser.getConnection()) {
            String sql = "SELECT judul, tenggat FROM tasktable WHERE user = ? AND DATE(tenggat) = DATE('now', '+1 day');";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, SessionManager.getInstance().getUsername());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("judul");
                String dueDate = rs.getString("tenggat");
                sendNotification("Task Deadline Besok!", "📌 " + title + " - deadline: " + dueDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendNotification(String title, String message) {
        try {
            if (!SystemTray.isSupported()) return;

            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");

            TrayIcon trayIcon = new TrayIcon(image, "Notifikasi Task");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("ToDo List");
            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
