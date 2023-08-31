package com.ltk.wordle.util;

import java.util.Comparator;

public interface IComboBoxItem extends Comparable<IComboBoxItem> {

    Comparator<IComboBoxItem> COMPARATOR_BY_DESCRIPTION = Comparator.comparing(iComboBoxItem -> iComboBoxItem.getComboBoxDescription() != null ? iComboBoxItem.getComboBoxDescription().trim().toLowerCase().replace(" ", "") : "");

    String getComboBoxDescription();

    @Override
    default int compareTo(IComboBoxItem item) {
        return COMPARATOR_BY_DESCRIPTION.compare(this, item);
    }

}
