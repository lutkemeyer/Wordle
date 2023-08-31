package com.ltk.wordle.screens.main;

import com.ltk.wordle.exceptions.WordleException;
import com.ltk.wordle.screens.AController;
import com.ltk.wordle.util.ComboBoxUtil;
import com.ltk.wordle.util.IComboBoxItem;
import com.ltk.wordle.util.IComboBoxItem2;
import com.ltk.wordle.util.SimpleComboBoxItem;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;

public class MainController extends AController implements IMainFacadeListener {

    @FXML
    private Label lblResults;

    @FXML
    private Label lblResultsQty;

    @FXML
    private TextField txfExistingChars;

    @FXML
    private TextField txfNonexistent;

    @FXML
    private TextField txfFixedChar1;

    @FXML
    private TextField txfFixedChar2;

    @FXML
    private TextField txfFixedChar3;

    @FXML
    private TextField txfFixedChar4;

    @FXML
    private TextField txfFixedChar5;

    @FXML
    private TextField txfWrongChars1;

    @FXML
    private TextField txfWrongChars2;

    @FXML
    private TextField txfWrongChars3;

    @FXML
    private TextField txfWrongChars4;

    @FXML
    private TextField txfWrongChars5;

    @FXML
    private ComboBox<IComboBoxItem2<Comparator<String>>> cbbSort;

    private final MainFacade facade = new MainFacade(this);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            facade.readWords();
            txfExistingChars.textProperty().addListener((obs, oldV, newV) -> facade.onExistingCharsChanged(newV));
            txfNonexistent.textProperty().addListener((obs, oldV, newV) -> facade.onNonexistentCharsChanged(newV));
            txfFixedChar1.textProperty().addListener((obs, oldV, newV) -> facade.onFixedCharChanged(0, getFirstChar(newV)));
            txfFixedChar2.textProperty().addListener((obs, oldV, newV) -> facade.onFixedCharChanged(1, getFirstChar(newV)));
            txfFixedChar3.textProperty().addListener((obs, oldV, newV) -> facade.onFixedCharChanged(2, getFirstChar(newV)));
            txfFixedChar4.textProperty().addListener((obs, oldV, newV) -> facade.onFixedCharChanged(3, getFirstChar(newV)));
            txfFixedChar5.textProperty().addListener((obs, oldV, newV) -> facade.onFixedCharChanged(4, getFirstChar(newV)));
            txfWrongChars1.textProperty().addListener((obs, oldV, newV) -> facade.onWrongCharsChanged(0, newV));
            txfWrongChars2.textProperty().addListener((obs, oldV, newV) -> facade.onWrongCharsChanged(1, newV));
            txfWrongChars3.textProperty().addListener((obs, oldV, newV) -> facade.onWrongCharsChanged(2, newV));
            txfWrongChars4.textProperty().addListener((obs, oldV, newV) -> facade.onWrongCharsChanged(3, newV));
            txfWrongChars5.textProperty().addListener((obs, oldV, newV) -> facade.onWrongCharsChanged(4, newV));
            cbbSort.setConverter(ComboBoxUtil.getConverter2(cbbSort));
            cbbSort.getItems().setAll(getSortOptions());
            cbbSort.getSelectionModel().select(0);
            cbbSort.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> facade.onSortChanged(Optional.ofNullable(newV).map(IComboBoxItem2::getObject).orElse(null)));
            facade.search();
        } catch (WordleException e) {
            e.printStackTrace();
        }
    }

    private Collection<IComboBoxItem2<Comparator<String>>> getSortOptions() {
        return Arrays.asList(
               new SimpleComboBoxItem<>("Ordem alfabética", MainFacade.ALPHABETIC_COMPARATOR),
               new SimpleComboBoxItem<>("Ordem por quantidade de vogais", MainFacade.VOLWELS_QUANTITY_COMPARATOR)
        );
    }

    @FXML
    public void onClickClear() {
        cbbSort.getSelectionModel().select(0);
        txfExistingChars.setText("");
        txfNonexistent.setText("");
        txfFixedChar1.setText("");
        txfFixedChar2.setText("");
        txfFixedChar3.setText("");
        txfFixedChar4.setText("");
        txfFixedChar5.setText("");
        txfWrongChars1.setText("");
        txfWrongChars2.setText("");
        txfWrongChars3.setText("");
        txfWrongChars4.setText("");
        txfWrongChars5.setText("");
    }

    private Character getFirstChar(String s) {
        return s != null ? s.charAt(0) : null;
    }

    @Override
    public void onFacadeListenerUpdateResults(Collection<String> filteredWords) {
        lblResults.setText(String.join("\n", filteredWords));
        lblResultsQty.setText("(" + filteredWords.size() + ")");
    }
    
}
