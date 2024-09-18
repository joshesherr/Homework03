module org.example.homework03 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.example.homework03 to javafx.fxml;
    exports org.example.homework03;
}