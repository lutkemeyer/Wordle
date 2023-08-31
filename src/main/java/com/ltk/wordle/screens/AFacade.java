package com.ltk.wordle.screens;

public abstract class AFacade {

    protected final IFacadeListener listener;

    protected AFacade(IFacadeListener listener) {
        this.listener = listener;
    }

}
