package com.erudit.exceptions;

/**
 * Created by zakhar on 22.04.2016.
 */
public class GameException extends Exception {

    GameException() {
        super();
    }

    GameException(String message) {
        super(message);
    }
}