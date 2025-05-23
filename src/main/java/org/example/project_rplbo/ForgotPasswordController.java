package org.example.project_rplbo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.project_rplbo.util.DBConnectionUser;

import java.sql.*;

public class ForgotPasswordController {

    @FXML
    private TextField usernameField;

    @FXML
    private Label securityQuestionLabel;

    @FXML
    private TextField answerField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Button resetButton;

    @FXML
    private Label messageLabel;

    private Connection conn;

    @FXML
    public void initialize() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:users.db");
        } catch (SQLException e) {
            messageLabel.setText("Gagal koneksi ke database.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGetSecurityQuestion() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            messageLabel.setText("Username tidak boleh kosong.");
            return;
        }

        try (Connection conn = DBConnectionUser.getConnection()) {
            String sql = "SELECT security_question FROM user WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String question = rs.getString("security_question");
                securityQuestionLabel.setText(question);
                answerField.setVisible(true);
                newPasswordField.setVisible(true);
                resetButton.setVisible(true);
                messageLabel.setText("");
            } else {
                messageLabel.setText("Username tidak ditemukan.");
                securityQuestionLabel.setText("");
                answerField.setVisible(false);
                newPasswordField.setVisible(false);
                resetButton.setVisible(false);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            messageLabel.setText("Terjadi kesalahan saat mengambil data.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleResetPassword() {
        String username = usernameField.getText().trim();
        String answer = answerField.getText().trim();
        String newPassword = newPasswordField.getText();

        if (answer.isEmpty() || newPassword.isEmpty()) {
            messageLabel.setText("Jawaban dan password baru harus diisi.");
            return;
        }

        try (Connection conn = DBConnectionUser.getConnection()) {
            String sql = "SELECT security_answer FROM user WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String correctAnswer = rs.getString("security_answer");
                if (correctAnswer.equalsIgnoreCase(answer)) {
                    // Update password (sebaiknya hash password di sini)
                    String updateSql = "UPDATE user SET password = ? WHERE username = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setString(1, newPassword);
                    updateStmt.setString(2, username);
                    int updated = updateStmt.executeUpdate();
                    if (updated > 0) {
                        messageLabel.setStyle("-fx-text-fill: green;");
                        messageLabel.setText("Password berhasil direset.");

                    } else {
                        messageLabel.setStyle("-fx-text-fill: red;");
                        messageLabel.setText("Gagal mereset password.");
                    }
                    updateStmt.close();
                } else {
                    messageLabel.setStyle("-fx-text-fill: red;");
                    messageLabel.setText("Jawaban pertanyaan keamanan salah.");
                }
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Username tidak ditemukan.");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            messageLabel.setText("Terjadi kesalahan saat reset password.");
            e.printStackTrace();
        }
    }

    @FXML
    void keLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Gagal Kembali");
        }
    }
}
