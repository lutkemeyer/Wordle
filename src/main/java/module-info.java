module com.example.wordle {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.ltk.wordle to javafx.fxml;

    exports com.ltk.wordle;
}