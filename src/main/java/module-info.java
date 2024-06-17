module com.example.lab20a {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.lab20a to javafx.fxml;
    exports com.example.lab20a;
}