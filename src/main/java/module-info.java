module com.example.wordle {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;

    opens com.ltk.wordle to javafx.fxml;

    exports com.ltk.wordle;
    exports com.ltk.wordle.screens.main;
    opens com.ltk.wordle.screens.main to javafx.fxml;
    exports com.ltk.wordle.util;
    opens com.ltk.wordle.util to javafx.fxml;
    exports com.ltk.wordle.screens;
    opens com.ltk.wordle.screens to javafx.fxml;
}