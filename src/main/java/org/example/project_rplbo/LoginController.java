package org.example.project_rplbo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.example.project_rplbo.util.DBConnectionUser;
import org.example.project_rplbo.util.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import java.sql.*;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private Connection connection;

    public void initialize() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:data_user.db");
            // Pastikan tabel user sudah ada
            String sqlCreate = "CREATE TABLE IF NOT EXISTS user (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "security_question TEXT NOT NULL," +
                    "security_answer TEXT NOT NULL" +
                    ")";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Gagal koneksi database");
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();

        String password = passwordField.getText();

        // Contoh validasi sederhana, sesuaikan dengan query database kamu
        try (Connection conn = DBConnectionUser.getConnection()) {
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Login berhasil, pindah ke halaman utama
                SessionManager.getInstance().login();
                SessionManager.getInstance().setUsername(username);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("TodoList.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Halaman Utama");
                stage.show();
            } else {
                // Login gagal
                // Tampilkan pesan error di label atau alert
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openTodoList() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("TodoList.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("ToDo List");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Gagal membuka ToDo List");
        }
    }

    @FXML
    private void handleForgotPassword() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("forgot_password.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Reset Password");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Gagal membuka form reset password");
        }
    }
    public class loginController {
        // method login
        public boolean login(String username, String password) {
            String sql = "SELECT * FROM user";
            try (Connection conn = DBConnectionUser.getConnection();
                 Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    if (username.equals(rs.getString("username")) && password.equals(rs.getString("password"))) {
                        return rs.next(); // true jika user ditemukan
                    }
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    @FXML
    private void handleShowRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrasi Akun");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Gagal membuka form registrasi.");
        }
    }


}
