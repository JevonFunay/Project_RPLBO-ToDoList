package org.example.project_rplbo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddTaskController {
    @FXML
    private Button btnTambah;

    @FXML
    private TextArea txtIsi;

    @FXML
    private TextField txtJudul;

    @FXML
    private DatePicker txtTenggat;

    private String url = "jdbc:sqlite:data_user.db";

    @FXML
    void onTambahClick(ActionEvent event) {
        String judul = txtJudul.getText();
        String isi = txtIsi.getText();
        String status = "Ongoing";
        LocalDate tenggat = txtTenggat.getValue();

        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "INSERT INTO tasktable (judul, isi, status, tenggat) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, judul);
                ps.setString(2, isi);
                ps.setString(3, status);
                ps.setString(4, tenggat.toString());
                ps.executeUpdate();
            }
        }
        catch (SQLException se) {
            System.out.println(se.getMessage());
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}

