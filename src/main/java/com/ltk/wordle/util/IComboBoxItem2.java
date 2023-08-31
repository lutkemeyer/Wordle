package com.ltk.wordle.util;

import java.util.Comparator;

public interface IComboBoxItem2<ITEM> extends Comparable<IComboBoxItem2> {

    Comparator<IComboBoxItem2> COMPARATOR_BY_DESCRIPTION = Comparator.comparing(iComboBoxItem -> iComboBoxItem.getComboBoxDescription() != null ? iComboBoxItem.getComboBoxDescription().trim().toLowerCase().replace(" ", "") : "");

    String getComboBoxDescription();

    ITEM getObject();

    @Override
    default int compareTo(IComboBoxItem2 item) {
        return COMPARATOR_BY_DESCRIPTION.compare(this, item);
    }

}
