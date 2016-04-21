package com.erudit.exceptions;

/**
 * Created by zakharov_ga on 13.04.2016.
 */
public class WordAlreadyUsedException extends Exception {
    String word;

    public WordAlreadyUsedException(String word) {
        this.word = word;
    }
}