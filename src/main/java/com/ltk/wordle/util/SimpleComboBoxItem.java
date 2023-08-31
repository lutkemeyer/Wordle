package com.ltk.wordle.util;

public class SimpleComboBoxItem<ITEM> implements IComboBoxItem2<ITEM> {

    private final String description;

    private final ITEM object;

    public SimpleComboBoxItem(String description, ITEM object) {
        this.description = description;
        this.object = object;
    }

    @Override
    public String getComboBoxDescription() {
        return description;
    }

    @Override
    public ITEM getObject() {
        return object;
    }

}
