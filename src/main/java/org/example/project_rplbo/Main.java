package org.example.project_rplbo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.project_rplbo.util.SessionManager;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        if (SessionManager.getInstance().isLoggedIn()) {
            Parent root = FXMLLoader.load(getClass().getResource("TodoList.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("To Do List");
            stage.setScene(scene);
        } else {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Login");
            stage.setScene(scene);
        }
        stage.show();
    }

    public static void changeScene(String fxmlFile) throws Exception {
        Parent pane = FXMLLoader.load(Main.class.getResource("Todolist.fxml" + fxmlFile));
        primaryStage.getScene().setRoot(pane);
    }

    public static void main(String[] args) {
        launch(args);
    }
}