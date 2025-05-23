package org.example.project_rplbo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.project_rplbo.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TodoListController implements Initializable {
    @FXML private TextField searchBar;
    @FXML private ListView<Task> activeListView;
    @FXML private ListView<Task> historyListView;
    @FXML private javafx.scene.control.Button btnLogout;
    @FXML private Label username;

    private final String url = "jdbc:sqlite:data_user.db";
    private final ObservableList<Task> activeTasks  = FXCollections.observableArrayList();
    private final ObservableList<Task> historyTasks = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup Username
        username.setText(SessionManager.getInstance().getUsername());

        // Setup cell factories for coloring
        setupCellFactory(activeListView,  true);
        setupCellFactory(historyListView, false);

        // Load data initially
        loadAllTasks();

        // Reload on search text change
        searchBar.textProperty().addListener((obs, oldV, newV) -> loadAllTasks());

        // Reload when window gains focus
        Platform.runLater(() -> {
            Stage stage = (Stage) activeListView.getScene().getWindow();
            stage.focusedProperty().addListener((o, was, isNow) -> {
                if (isNow) loadAllTasks();
            });
        });
    }

    /**
     * Configure cell factory for ListView: active lists (blue) vs history (red/green)
     */
    private void setupCellFactory(ListView<Task> lv, boolean isActiveList) {
        lv.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Task t, boolean empty) {
                super.updateItem(t, empty);
                if (empty || t == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(t.getJudul() + " (" + t.getStatus() + ")");
                    if ("Ongoing".equals(t.getStatus())) {
                        setTextFill(Color.BLUE);
                    }else if ("Cancel".equalsIgnoreCase(t.getStatus())) {
                        setTextFill(Color.RED);
                    } else if ("Selesai".equalsIgnoreCase(t.getStatus())) {
                        setTextFill(Color.GREEN);
                    } else if ("Pending".equalsIgnoreCase(t.getStatus())) {
                        setTextFill(Color.ORANGE);
                    } else {
                        setTextFill(Color.BLACK);
                    }
                    }
                }
        });
    }

    private void loadAllTasks() {
        activeTasks.clear();
        historyTasks.clear();

        String sql = "SELECT * FROM tasktable WHERE "
                + "LOWER(judul) LIKE ? OR LOWER(isi) LIKE ? OR LOWER(tenggat) LIKE ?";
        String keyword = "%" + searchBar.getText().trim().toLowerCase() + "%";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, keyword);
            ps.setString(2, keyword);
            ps.setString(3, keyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // —–––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
                    // **Perbaikan di sini**: pastikan urutan: judul, status, isi, tenggat
                    if (SessionManager.getInstance().getUsername().equals(rs.getString("user"))) {
                        Task t = new Task(
                                rs.getString("judul"),
                                rs.getString("status"),
                                rs.getString("isi"),
                                rs.getString("tenggat")
                        );
                        String st = t.getStatus().toLowerCase();
                        if (st.equals("ongoing") || st.equals("pending")) {
                            activeTasks.add(t);
                        } else {
                            historyTasks.add(t);
                        }
                    }
                    // —–––––––––––––––––––––––––––––––––––––––––––––––––––––––––––

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        activeListView.setItems(activeTasks);
        historyListView.setItems(historyTasks);
    }

    @FXML
    void searchFilter(ActionEvent event) {
        // Simply reload all tasks with current filter
        loadAllTasks();
    }

    @FXML
    void onRefresh(ActionEvent event) {
        loadAllTasks();
    }

    @FXML
    void handleAddTask(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("addTask.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Tambah Tugas");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal membuka jendela tambah tugas").showAndWait();
        }
    }

    @FXML
    void handleRemoveTask(ActionEvent event) {
        Task selected = activeListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Pilih tugas yang akan dihapus").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus tugas ini?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try (Connection conn = DriverManager.getConnection(url);
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM tasktable WHERE judul = ? AND tenggat = ?")) {
                    ps.setString(1, selected.getJudul());
                    ps.setString(2, selected.getTenggat());
                    ps.executeUpdate();
                    loadAllTasks();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void handleEditOnDoubleClick(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            Task sel = activeListView.getSelectionModel().getSelectedItem();
            if (sel != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("editTask.fxml"));
                    Parent root = loader.load();
                    EditTaskController ctrl = loader.getController();
                    ctrl.setTask(sel);
                    Stage stage = new Stage();
                    stage.setTitle("Edit Tugas");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void onLogout(ActionEvent event) {
        try {
            SessionManager.getInstance().logout();
            SessionManager.getInstance().setUsername(null);
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal logout").showAndWait();
        }
    }

    @FXML
    private void handleHistoryDoubleClick(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            Task sel = historyListView.getSelectionModel().getSelectedItem();
            if (sel != null) {
                try {
                    // Load FXML detail (viewTask.fxml)
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("viewTask.fxml"));
                    Parent root = loader.load();

                    // Kirim Task ke controller view-only
                    ViewTaskController ctrl = loader.getController();
                    ctrl.setTask(sel);

                    // Tampilkan di window baru
                    Stage stage = new Stage();
                    stage.setTitle("Detail Tugas");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Gagal membuka detail tugas").showAndWait();
                }
            }
        }
    }
}
