package org.example.project_rplbo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.example.project_rplbo.util.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddTaskController implements Initializable {
    @FXML private TextField txtJudul;
    @FXML private TextArea txtIsi;
    @FXML private ChoiceBox<String> statusChoiceBox;
    @FXML private DatePicker txtTenggat;
    @FXML private ChoiceBox<String> kategoriChoice;

    private final String url = "jdbc:sqlite:data_user.db";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inisialisasi opsi status
        if (statusChoiceBox.getItems().isEmpty()) {
            statusChoiceBox.getItems().addAll("Ongoing", "Pending", "Selesai", "Cancel");
            // Default status
        }
        statusChoiceBox.setValue("Ongoing");

        // Inisialisasi kategori
        if (kategoriChoice.getItems().isEmpty()) {
            kategoriChoice.getItems().addAll("Hiburan", "Self-Development", "Kuliah", "Lainnya");
        }
        kategoriChoice.setValue("Self-Development");
    }
    /**
     * Handle tombol Tambah: simpan task baru ke database lalu tutup window
     */
    @FXML
    private void onAdd(ActionEvent event) {
        String judul = txtJudul.getText().trim();
        String isi   = txtIsi.getText().trim();
        String status= statusChoiceBox.getValue();
        String tenggat = txtTenggat.getValue() != null ? txtTenggat.getValue().toString() : "";
        String kategori = kategoriChoice.getValue();
        String user = SessionManager.getInstance().getUsername();

        if (judul.isEmpty() || isi.isEmpty() || tenggat.isEmpty() || kategori == null) {
            new Alert(Alert.AlertType.WARNING, "Semua field harus diisi.").showAndWait();
            return;
        }

        // Tambahkan kolom kategori ke query
        String sql = "INSERT INTO tasktable (judul, isi, status, tenggat, kategori, user) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, judul);
            ps.setString(2, isi);
            ps.setString(3, status);
            ps.setString(4, tenggat);
            ps.setString(5, kategori);
            ps.setString(6, user);
            int inserted = ps.executeUpdate();
            if (inserted > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Tugas berhasil ditambahkan.").showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal menambahkan tugas.").showAndWait();
        }
// Tutup window setelah menambah
        Stage stage = (Stage) txtJudul.getScene().getWindow();
        stage.close();
    }
    /**
     * Handle tombol Batal: tutup window tanpa aksi
     */
    @FXML
    private void onCancel(ActionEvent event) {
        Stage stage = (Stage) txtJudul.getScene().getWindow();
        stage.close();
    }
}
