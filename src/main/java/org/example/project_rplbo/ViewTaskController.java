package org.example.project_rplbo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ViewTaskController {
    @FXML private Label lblJudul;
    @FXML private TextArea txtIsi;
    @FXML private Label lblStatus;
    @FXML private Label lblTenggat;

    private Task task;

    public void setTask(Task task) {
        this.task = task;
        // isi data ke UI
        lblJudul.setText(task.getJudul());
        txtIsi.setText(task.getIsi());
        lblStatus.setText(task.getStatus());
        lblTenggat.setText(task.getTenggat());
    }

    @FXML
    private void onClose() {
        ((Stage) lblJudul.getScene().getWindow()).close();
    }
}
