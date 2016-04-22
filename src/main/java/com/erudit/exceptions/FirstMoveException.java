package com.erudit.exceptions;

/**
 * Created by zakharov_ga on 13.04.2016.
 */
public class FirstMoveException extends GameException {

    public FirstMoveException() {
        super("Первый ход должен включать центральную клетку!");
    }
}