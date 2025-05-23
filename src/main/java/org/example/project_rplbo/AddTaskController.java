package org.example.project_rplbo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
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

    private final String url = "jdbc:sqlite:data_user.db";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inisialisasi opsi status
        if (statusChoiceBox.getItems().isEmpty()) {
            statusChoiceBox.getItems().addAll("Ongoing", "Pending", "Selesai", "Cancel");
        }
        // Default status
        statusChoiceBox.setValue("Ongoing");
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

        if (judul.isEmpty() || isi.isEmpty() || tenggat.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Semua field harus diisi.").showAndWait();
            return;
        }

        String sql = "INSERT INTO tasktable (judul, isi, status, tenggat) VALUES (?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, judul);
            ps.setString(2, isi);
            ps.setString(3, status);
            ps.setString(4, tenggat);
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
