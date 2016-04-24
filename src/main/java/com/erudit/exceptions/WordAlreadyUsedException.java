package com.erudit.exceptions;

/**
 * Created by zakharov_ga on 13.04.2016.
 */
public class WordAlreadyUsedException extends GameException {

    private String word;

    public WordAlreadyUsedException(String word) {
        super("Слово уже использовано:");
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}