package com.erudit.messages;

/**
 * Created by zakhar on 27.03.2016.
 */
public class TimeOverMessage extends Message {
    private String player;
    private String nextMove;

    public TimeOverMessage(String player, String nextMove) {
        super("timeOver");
        this.player = player;
        this.nextMove = nextMove;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getNextMove() {
        return nextMove;
    }

    public void setNextMove(String nextMove) {
        this.nextMove = nextMove;
    }
}