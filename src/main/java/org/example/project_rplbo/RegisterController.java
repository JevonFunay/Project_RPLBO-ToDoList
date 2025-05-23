package org.example.project_rplbo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.example.project_rplbo.util.DBConnectionUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField securityQuestionField;

    @FXML
    private TextField securityAnswerField;

    @FXML
    private Label messageLabel;

    private Connection connection;

    @FXML
    public void initialize() throws SQLException {
        connection = DBConnectionUser.getConnection(); // Pastikan kelas ini mengembalikan koneksi DB
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String question = securityQuestionField.getText().trim();
        String answer = securityAnswerField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || question.isEmpty() || answer.isEmpty()) {
            messageLabel.setText("Semua field harus diisi");
            return;
        }

        try {
            String sql = "INSERT INTO user (username, password, security_question, security_answer) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, question);
            stmt.setString(4, answer);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Registrasi berhasil! Silakan login.");

                // Kembali ke halaman login
                Stage stage = (Stage) usernameField.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
                stage.setScene(new Scene(root));
                stage.setTitle("Login");
            } else {
                messageLabel.setText("Gagal registrasi");
            }
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Terjadi kesalahan saat registrasi");
        }
    }

    @FXML
    private void handleShowLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Gagal membuka halaman login");
        }
    }
}
