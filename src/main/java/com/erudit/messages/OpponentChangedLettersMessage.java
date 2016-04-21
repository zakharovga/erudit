package com.erudit.messages;

import com.erudit.Game;

/**
 * Created by zakharov_ga on 24.03.2016.
 */
public class OpponentChangedLettersMessage extends Message {

    private String opponent;

    private String nextMove;

    public OpponentChangedLettersMessage(Game game, String username) {
        super("opponentChangedLetters");
        opponent = username;
        nextMove = game.getNextMove().getUsername();
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getNextMove() {
        return nextMove;
    }

    public void setNextMove(String nextMove) {
        this.nextMove = nextMove;
    }
}