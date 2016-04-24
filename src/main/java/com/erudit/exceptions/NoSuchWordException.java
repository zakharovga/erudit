package com.erudit.exceptions;

/**
 * Created by zakharov_ga on 13.04.2016.
 */
public class NoSuchWordException extends GameException {

    private String word;

    public NoSuchWordException(String word) {
        super("Слово не найдено в словаре:");
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}