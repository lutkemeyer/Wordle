package com.ltk.wordle.screens;

import java.util.Collection;

public interface IFacadeListener {
    void onFacadeListenerUpdateResults(Collection<String> filteredWords);

}
