package com.erudit.exceptions;

/**
 * Created by zakharov_ga on 13.04.2016.
 */
public class NoSuchWordException extends Exception {
    String word;

    public NoSuchWordException(String word) {
        this.word = word;
    }
}