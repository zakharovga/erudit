package com.erudit.messages;

import com.erudit.User;

/**
 * Created by zakharov_ga on 02.03.2016.
 */
public class OpponentJoinedMessage extends Message {

    private User player;

    public OpponentJoinedMessage(User player) {
        super("opponentJoined");
        this.player = player;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }
}