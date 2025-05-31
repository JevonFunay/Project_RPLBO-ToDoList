package org.example.project_rplbo;

import javafx.event.ActionEvent; // Anda mungkin memerlukan ini jika tombol onClose menggunakan ActionEvent
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ViewTaskController {
    @FXML private Label lblJudul;
    @FXML private TextArea txtIsi;
    @FXML private Label lblStatus;
    @FXML private Label lblTenggat;
    @FXML private Label lblKategori; // <--- TAMBAHKAN BARIS INI

    private Task task; // Tidak apa-apa menyimpan referensi task jika diperlukan nanti

    public void setTask(Task task) {
        this.task = task;
        // isi data ke UI
        if (task != null) { // Selalu baik untuk memeriksa null
            lblJudul.setText(task.getJudul() != null ? task.getJudul() : "-");
            txtIsi.setText(task.getIsi() != null ? task.getIsi() : "-");
            lblStatus.setText(task.getStatus() != null ? task.getStatus() : "-");
            lblTenggat.setText(task.getTenggat() != null ? task.getTenggat() : "-");

            // --- SET KATEGORI DI SINI ---
            lblKategori.setText(task.getKategori() != null && !task.getKategori().isEmpty() ? task.getKategori() : "-");
        } else {
            // Opsional: Bersihkan field jika task null
            lblJudul.setText("-");
            txtIsi.setText("");
            lblStatus.setText("-");
            lblTenggat.setText("-");
            lblKategori.setText("-");
        }
    }

    @FXML
    private void onClose() { // Metode ini sudah benar
        // Anda bisa juga menggunakan ActionEvent event sebagai parameter jika onAction="#onClose" di FXML mengharapkannya
        // Namun, karena tidak ada 'event' yang digunakan di dalam metode, ini sudah cukup.
        Stage stage = (Stage) lblJudul.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}