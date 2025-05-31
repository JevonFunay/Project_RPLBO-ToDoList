package org.example.project_rplbo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.project_rplbo.util.NotificationUtil;
import org.example.project_rplbo.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class TodoListController implements Initializable {
    @FXML private TextField searchBar;
    @FXML private ListView<Task> activeListView;
    @FXML private ListView<Task> historyListView;
    @FXML private javafx.scene.control.Button btnLogout;
    @FXML private Label username;
    @FXML private Button btnToggleTheme;
    private boolean isDarkMode = false;
    private String lightModeCss;
    private String darkModeCss;
    private String fxmlDefaultLightModeCssPath; // Untuk referensi stylesheet default FXML
    private static final String THEME_PREF_KEY = "appThemeTodoList";
    private final String url = "jdbc:sqlite:data_user.db";
    private final ObservableList<Task> activeTasks  = FXCollections.observableArrayList();
    private final ObservableList<Task> historyTasks = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // PATH ABSOLUT (awali slash) mengarah ke resources/org/example/project_rplbo/styles/…
            String lightCssPath = "/org/example/project_rplbo/styles/dashboardStyles.css";
            String darkCssPath  = "/org/example/project_rplbo/styles/darkDashboardStyles.css";

            URL lightCssUrl = getClass().getResource(lightCssPath);
            URL darkCssUrl  = getClass().getResource(darkCssPath);

            if (lightCssUrl == null || darkCssUrl == null) {
                throw new NullPointerException("File CSS tema tidak ditemukan.");
            }
            lightModeCss = lightCssUrl.toExternalForm();
            darkModeCss  = darkCssUrl.toExternalForm();
        } catch (NullPointerException e) {
            // Jika gagal, nonaktifkan tombol toggle
            lightModeCss = null;
            darkModeCss  = null;
            if (btnToggleTheme != null) {
                btnToggleTheme.setDisable(true);
                btnToggleTheme.setText("Tema Error");
            }
        }
        // Setup Username
        username.setText(SessionManager.getInstance().getUsername());

        // Setup cell factories for coloring
        setupCellFactory(activeListView,  true);
        setupCellFactory(historyListView, false);

        // Load data initially
        loadAllTasks();

        // Notification h-1
        NotificationUtil.checkForDueTasks();

        // Reload on search text change
        searchBar.textProperty().addListener((obs, oldV, newV) -> loadAllTasks());

        // Reload when window gains focus
        Platform.runLater(() -> {
            // Pastikan scene sudah ada sebelum mencoba memodifikasinya
            if (activeListView != null && activeListView.getScene() != null) {
                loadThemePreference(activeListView.getScene());
                Stage stage = (Stage) activeListView.getScene().getWindow();
                if (stage != null) {
                    stage.focusedProperty().addListener((o, was, isNow) -> {
                        if (isNow) loadAllTasks();
                    });
                }
            } else {
                System.err.println("Peringatan: Scene belum tersedia saat akhir initialize(). Tema awal mungkin tidak teraplikasi dengan benar.");
                // Jika btnToggleTheme ada, bisa coba ambil scene dari sana, tapi mungkin juga null.
            }
        });
    }

    /**
     * Configure cell factory for ListView: active lists (blue) vs history (red/green)
     */
    private void loadThemePreference(Scene scene) {
        if (lightModeCss == null || darkModeCss == null) return;
        Preferences prefs = Preferences.userNodeForPackage(TodoListController.class);
        String theme = prefs.get(THEME_PREF_KEY, "light");
        isDarkMode = "dark".equals(theme);

            // Hapus semua CSS lama
        scene.getStylesheets().clear();

        if (isDarkMode) {
            scene.getStylesheets().add(darkModeCss);
            btnToggleTheme.setText("Light Mode");
        } else {
            scene.getStylesheets().add(lightModeCss);
               btnToggleTheme.setText("Dark Mode");
        }
    }
    @FXML
    void handleToggleTheme(ActionEvent event) {
        if (lightModeCss == null || darkModeCss == null) return;

        Scene scene = ((Node) event.getSource()).getScene();
        if (scene == null) return;

        // Hapus dulu
        scene.getStylesheets().clear();

        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            scene.getStylesheets().add(darkModeCss);
            btnToggleTheme.setText("Light Mode");
            Preferences.userNodeForPackage(TodoListController.class).put(THEME_PREF_KEY, "dark");
        } else {
            scene.getStylesheets().add(lightModeCss);
            btnToggleTheme.setText("Dark Mode");
            Preferences.userNodeForPackage(TodoListController.class).put(THEME_PREF_KEY, "light");
        }
    }
    private void setupCellFactory(ListView<Task> lv, boolean isActiveList) {
        lv.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Task t, boolean empty) {
                super.updateItem(t, empty);
                if (empty || t == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setPadding(new Insets(10));
                    setText(t.getJudul() + " (" + t.getStatus() + ")");
                    if ("Ongoing".equalsIgnoreCase(t.getStatus())) { // Menggunakan equalsIgnoreCase untuk konsistensi
                        setTextFill(Color.BLUE);
                    } else if ("Cancel".equalsIgnoreCase(t.getStatus())) {
                        setTextFill(Color.RED);
                    } else if ("Selesai".equalsIgnoreCase(t.getStatus())) {
                        setTextFill(Color.GREEN);
                    } else if ("Pending".equalsIgnoreCase(t.getStatus())) {
                        setTextFill(Color.ORANGE);
                    } else if ("Dihapus".equalsIgnoreCase(t.getStatus())) { // <-- BARIS BARU
                        setTextFill(Color.GRAY);                        // <-- WARNA BARU ABU-ABU
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
                                rs.getString("tenggat"),
                                rs.getString("kategori")
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
            new Alert(Alert.AlertType.WARNING, "Pilih tugas yang akan dipindahkan ke Riwayat.").showAndWait();
            return;
        }

        // Pesan konfirmasi yang lebih deskriptif
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Apakah Anda yakin ingin memindahkan tugas '" + selected.getJudul() + "' ke Riwayat dengan status 'Dihapus'?");
        confirm.setTitle("Konfirmasi Pemindahan Tugas");
        confirm.setHeaderText("Pindahkan Tugas ke Riwayat"); // Header opsional

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                // Menggunakan ID unik task akan lebih aman jika ada.
                // Untuk saat ini, kita asumsikan judul, tenggat, dan user cukup unik.
                String sql = "UPDATE tasktable SET status = ? WHERE judul = ? AND tenggat = ? AND user = ?";
                try (Connection conn = DriverManager.getConnection(url);
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, "Dihapus"); // Status baru
                    ps.setString(2, selected.getJudul());
                    ps.setString(3, selected.getTenggat());
                    ps.setString(4, SessionManager.getInstance().getUsername()); // Penting untuk memastikan task milik user yang benar

                    int affectedRows = ps.executeUpdate();
                    if (affectedRows > 0) {
                        loadAllTasks(); // Muat ulang data untuk memperbarui kedua ListView
                        new Alert(Alert.AlertType.INFORMATION, "Tugas telah dipindahkan ke Riwayat.").showAndWait();
                    } else {
                        // Ini bisa terjadi jika task tidak ditemukan (misalnya sudah dihapus/diedit oleh proses lain)
                        new Alert(Alert.AlertType.WARNING, "Tugas tidak ditemukan atau gagal diperbarui.").showAndWait();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Terjadi kesalahan database: " + e.getMessage()).showAndWait();
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
