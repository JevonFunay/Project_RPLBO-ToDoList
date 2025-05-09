package org.example.project_rplbo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class EditTaskController {

    @FXML
    private Button btnBatal;

    @FXML
    private Button btnSimpan;

    @FXML
    private TextArea txtIsi;

    @FXML
    private TextField txtJudul;

    @FXML
    private DatePicker txtTenggat;

    @FXML
    private ChoiceBox<String> statusChoice;

    private Task tasks;
    private String url = "jdbc:sqlite:data_user.db";

    public void setTask(Task tasks) {
        this.tasks = tasks;
        txtJudul.setText(tasks.getJudul());
        txtIsi.setText(tasks.getIsi());
        statusChoice.getItems().addAll("Ongoing", "Pending", "Cancel", "Selesai");
        statusChoice.setValue(tasks.getStatus());
        txtTenggat.setValue(LocalDate.parse(tasks.getTenggat()));
    }

    @FXML
    void onSimpanClick(ActionEvent event) throws SQLException {
        String newJudul = txtJudul.getText();
        String newIsi = txtIsi.getText();
        String newStatus = statusChoice.getValue();
        LocalDate newTenggat = txtTenggat.getValue();

        String sql = "UPDATE tasktable SET judul = ?, isi = ?, status = ?, tenggat = ? WHERE judul = ?";
        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newJudul);
            ps.setString(2, newIsi);
            ps.setString(3, newStatus);
            ps.setString(4, newTenggat.toString());
            ps.setString(5, tasks.getJudul());  // pakai judul lama untuk WHERE
            ps.executeUpdate();
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void onBatalClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}

