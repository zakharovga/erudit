package com.erudit.exceptions;

import com.erudit.Move;

import java.util.Set;

/**
 * Created by zakharov_ga on 13.04.2016.
 */
public class IncorrectMoveException extends Exception {

    Set<Move> incorrectMoves;

    public IncorrectMoveException(Set<Move> incorrectMoves) {
        super("Буквы выставлены некорректно!");
        this.incorrectMoves = incorrectMoves;
    }

    public Set<Move> getIncorrectMoves() {
        return incorrectMoves;
    }
}