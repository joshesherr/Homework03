module org.example.homework03 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.homework03 to javafx.fxml;
    exports org.example.homework03;
}