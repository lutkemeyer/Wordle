package com.ltk.wordle.util;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class ComboBoxUtil {

    public static final String SELECIONE_PLACEHOLDER = "SELECIONE";

    public static StringConverter<IComboBoxItem> getConverter(ComboBox<IComboBoxItem> comboBox) {
        return new StringConverter<>() {
            @Override
            public String toString(IComboBoxItem item) {
                return item != null ? item.getComboBoxDescription() : "";
            }
            @Override
            public IComboBoxItem fromString(String string) {
                return comboBox.getItems()
                        .stream()
                        .filter(f -> f.getComboBoxDescription().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        };
    }

    public static <ITEM> StringConverter<IComboBoxItem2<ITEM>> getConverter2(ComboBox<IComboBoxItem2<ITEM>> comboBox) {
        return new StringConverter<>() {
            @Override
            public String toString(IComboBoxItem2<ITEM> item) {
                return item != null ? item.getComboBoxDescription() : "";
            }
            @Override
            public IComboBoxItem2<ITEM> fromString(String string) {
                return comboBox.getItems()
                        .stream()
                        .filter(f -> f.getComboBoxDescription().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        };
    }

}
