module org.example.project_prlbo {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.project_prlbo to javafx.fxml;
    exports org.example.project_prlbo;
}