package org.example.project_rplbo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class TodoListController {

    @FXML
    private TextField searchBar;

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

        // Real-Time Search
        searchBar.setOnKeyReleased(event -> {
            try {
                searchFilter(null);
            } catch (Exception e) {
                e.printStackTrace();
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

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("editTask.fxml"));
                        Scene scene = new Scene(loader.load());
                        Stage stage = new Stage();
                        stage.setTitle("Edit Task");
                        stage.setScene(scene);

                        EditTaskController controller = loader.getController();
                        controller.setTask(selected);

                        stage.setOnHiding(e -> getAllData());  // refresh setelah form ditutup
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
    void searchFilter(ActionEvent event) throws SQLException, ClassNotFoundException {
        tasks.clear();
        String searchText = searchBar.getText().trim().toLowerCase();

        // Jika kosong, tampilkan semua data
        if (searchText.isEmpty()) {
            getAllData();
            return;
        }

        String sql = "SELECT * FROM tasktable WHERE " +
                "LOWER(judul) LIKE ? OR " +
                "LOWER(isi) LIKE ? OR " +
                "LOWER(tenggat) LIKE ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String likePattern = "%" + searchText + "%";
            ps.setString(1, likePattern);
            ps.setString(2, likePattern);
            ps.setString(3, likePattern);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Task tsk = new Task(
                        rs.getString("judul"),
                        rs.getString("isi"),
                        rs.getString("status"),
                        rs.getString("tenggat")
                );
                tasks.add(tsk);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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