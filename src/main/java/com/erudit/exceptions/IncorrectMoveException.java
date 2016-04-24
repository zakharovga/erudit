package com.erudit.exceptions;

import com.erudit.Move;

import java.util.Set;

/**
 * Created by zakharov_ga on 13.04.2016.
 */
public class IncorrectMoveException extends GameException {

    private Set<Move> incorrectMoves;

    public IncorrectMoveException(Set<Move> incorrectMoves) {
        super("Следующие буквы выставлены не по правилам:");
        this.incorrectMoves = incorrectMoves;
    }

    public Set<Move> getIncorrectMoves() {
        return incorrectMoves;
    }
}