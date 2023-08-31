package com.ltk.wordle.exceptions;

public class WordleException extends Exception {

    public WordleException(String message) {
        super(message);
    }

    public WordleException(String message, Throwable cause) {
        super(message, cause);
    }

}
