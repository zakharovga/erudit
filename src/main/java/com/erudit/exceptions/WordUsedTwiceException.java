package com.erudit.exceptions;

/**
 * Created by zakharov_ga on 13.04.2016.
 */
public class WordUsedTwiceException extends GameException {
    private String word;

    public WordUsedTwiceException(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}