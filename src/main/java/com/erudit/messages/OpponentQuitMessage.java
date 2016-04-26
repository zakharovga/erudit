package com.erudit.messages;

/**
 * Created by zakharov_ga on 09.03.2016.
 */
public class OpponentQuitMessage extends Message {

    private String opponent;

    public OpponentQuitMessage(String username) {
        super("OPPONENT_QUIT");
        this.opponent = username;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }
}