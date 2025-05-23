package org.example.project_rplbo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditTaskController {
    @FXML private TextField txtJudul;
    @FXML private TextArea txtIsi;
    @FXML private ChoiceBox<String> statusChoice;
    @FXML private DatePicker txtTenggat;

    private Task task;
    private final String url = "jdbc:sqlite:data_user.db";

    /**
     * Dipanggil saat controller di-load untuk menyiapkan ChoiceBox
     */
    @FXML
    public void initialize() {
        // Isi pilihan status jika belum ada
        if (statusChoice.getItems().isEmpty()) {
            statusChoice.getItems().addAll("Ongoing", "Pending", "Selesai", "Cancel");
        }
    }

    /**
     * Set data Task ke UI sebelum edit
     */
    public void setTask(Task task) {
        this.task = task;
        txtJudul.setText(task.getJudul());
        txtIsi.setText(task.getIsi());
        statusChoice.setValue(task.getStatus());
        txtTenggat.setValue(java.time.LocalDate.parse(task.getTenggat()));
    }

    /**
     * Handle tombol Update: simpan perubahan ke database lalu tutup window
     */
    @FXML
    private void onUpdate(ActionEvent event) {
        String newJudul = txtJudul.getText().trim();
        String newIsi   = txtIsi.getText().trim();
        String newStatus = statusChoice.getValue();
        String newTenggat = txtTenggat.getValue().toString();

        String sql = "UPDATE tasktable SET judul=?, isi=?, status=?, tenggat=? WHERE judul=? AND tenggat=?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newJudul);
            ps.setString(2, newIsi);
            ps.setString(3, newStatus);
            ps.setString(4, newTenggat);
            // where clause uses original values
            ps.setString(5, task.getJudul());
            ps.setString(6, task.getTenggat());
            int updated = ps.executeUpdate();
            if (updated > 0) {
                // tampilkan notifikasi sukses
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Tugas berhasil diperbarui.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal memperbarui tugas.").showAndWait();
        }

        // Tutup window setelah update
        Stage stage = (Stage) txtJudul.getScene().getWindow();
        stage.close();
    }

    /**
     * Handle tombol Batal: tutup window tanpa menyimpan
     */
    @FXML
    private void onCancel(ActionEvent event) {
        Stage stage = (Stage) txtJudul.getScene().getWindow();
        stage.close();
    }
}