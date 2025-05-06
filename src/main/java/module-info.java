module org.example.project_rplbo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.project_rplbo to javafx.fxml;
    exports org.example.project_rplbo;
}