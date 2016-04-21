package com.erudit.messages;

/**
 * Created by zakharov_ga on 10.03.2016.
 */
public class OpponentReadyMessage extends Message {

    private String opponent;
    private boolean ready;

    public OpponentReadyMessage(String opponent, boolean ready) {
        super("opponentReady");
        this.opponent = opponent;
        this.ready = ready;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String player) {
        this.opponent = player;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}