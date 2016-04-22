package com.erudit.messages;

import com.erudit.Move;

import java.util.Set;

/**
 * Created by zakhar on 22.04.2016.
 */
public class IncorrectMoveExceptionMessage extends ExceptionMessage {

    private Set<Move> incorrectMoves;

    public IncorrectMoveExceptionMessage(String action, String message, Set<Move> moves) {
        super(action, message);
        incorrectMoves = moves;
    }

    public Set<Move> getIncorrectMoves() {
        return incorrectMoves;
    }

    public void setIncorrectMoves(Set<Move> incorrectMoves) {
        this.incorrectMoves = incorrectMoves;
    }
}