package org.example.project_rplbo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class TodoListController {

//    @FXML
//    private TextField taskInput;

    @FXML
    private ListView<Task> taskListView;

    private ObservableList<Task> tasks;

    private String url = "jdbc:sqlite:data_user.db";

    @FXML
    public void initialize() {
        tasks = FXCollections.observableArrayList();
        taskListView.setItems(tasks);

        // Tidak perlu isi priorityComboBox karena status otomatis "Ongoing"

        // Custom cell factory untuk menampilkan tugas dengan warna status
        taskListView.setCellFactory(lv -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTextFill(Color.BLACK);
                } else {
                    setText(item.getJudul() + " (" + item.getStatus() + ")");
                    setTextFill(getColorForStatus(item.getStatus()));
                }
            }
        });

        // Mengubah status tugas saat dipilih di list dengan double click
        taskListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Task selected = taskListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    Alert infoDialog = new Alert(Alert.AlertType.INFORMATION);
                    infoDialog.setTitle("Detail Tugas");
                    infoDialog.setHeaderText("Judul: " + selected.getJudul());
                    infoDialog.setContentText(
                            "Isi: " + selected.getIsi() + "\n\n" +
                                    "Tenggat: " + selected.getTenggat()
                    );
                    infoDialog.showAndWait();
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(selected.getStatus(), "Ongoing", "Pending", "Cancel", "Selesai");
                    dialog.setTitle("Ubah Status");
                    dialog.setHeaderText("Ubah status tugas");
                    dialog.setContentText("Pilih status baru:");

                    dialog.showAndWait().ifPresent(newStatus -> {
                        selected.setStatus(newStatus);
                        String sql = "UPDATE tasktable SET status = ? WHERE judul = ?";
                        try (Connection conn = DriverManager.getConnection(url)) {
                            PreparedStatement ps = conn.prepareStatement(sql);
                            ps.setString(1, newStatus);
                            ps.setString(2, selected.getJudul());
                            ps.executeUpdate();
                        } catch (SQLException se) {
                            System.out.println(se.getMessage());
                        }
                        taskListView.refresh();
                    });
                }
            }
        });
        getAllData();
    }

    @FXML
    private void onRefresh() {
        getAllData();
    }

    @FXML
    private void handleAddTask() throws IOException {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("addTask.fxml")));
        Stage stage = new Stage();
        stage.setTitle("Add Task");
        stage.setScene(scene);
        stage.show();

        getAllData();
//        String desc = taskInput.getText().trim();
//        if (!desc.isEmpty()) {
//            // Status langsung di-set "Ongoing"
//            tasks.add(new Task(desc, "Ongoing"));
//            taskInput.clear();
//        }
    }

    @FXML
    private void handleRemoveTask() {
        Task selected = taskListView.getSelectionModel().getSelectedItem();
        String sql = "DELETE FROM tasktable WHERE judul = ?";
        try (Connection conn = DriverManager.getConnection(url)) {
            if (selected != null) {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, selected.getJudul());
                ps.executeUpdate();
            }
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
//      tasks.remove(selected);
        getAllData();
    }

    public void getAllData() {
        tasks.clear();
        String sql = "SELECT * FROM tasktable";
        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                tasks.add(new Task(rs.getString("judul"), rs.getString("status"), rs.getString("isi"), rs.getString("tenggat")));
            }
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }

    private Color getColorForStatus(String status) {
        switch (status.toLowerCase()) {
            case "ongoing":
                return Color.BLUE;
            case "pending":
                return Color.ORANGE;
            case "cancel":
                return Color.RED;
            case "selesai":
                return Color.GREEN;
            default:
                return Color.BLACK;
        }
    }
}
//    public static class Task {
//        private String judul;
//        private String status;
//        private String isi;
//        private String tenggat;
//
//        public Task(String judul, String status, String isi, String tenggat) {
//            this.judul = judul;
//            this.status = status;
//            this.isi = isi;
//            this.tenggat = tenggat;
//        }
//
//        public String getJudul() {
//            return judul;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getIsi() { return isi ; }
//        public String getTenggat() { return tenggat ; }
//    }
//}
