package com.erudit.messages;

/**
 * Created by zakharov_ga on 09.03.2016.
 */
public class OpponentQuitMessage extends Message {

    private String opponent;
    private String nextMove;

    public OpponentQuitMessage(String username) {
        super("OPPONENT_QUIT");
        this.opponent = username;
    }

    public OpponentQuitMessage(String opponent, String nextMove) {
        super("OPPONENT_QUIT");
        this.opponent = opponent;
        this.nextMove = nextMove;
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