module org.example.project_rplbo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.prefs;


    opens org.example.project_rplbo to javafx.fxml;
    exports org.example.project_rplbo;
}