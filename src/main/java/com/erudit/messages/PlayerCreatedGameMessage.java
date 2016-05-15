package com.erudit.messages;

import com.erudit.User;

/**
 * Created by zakhar on 15.05.2016.
 */
public class PlayerCreatedGameMessage extends Message {

    private User player;

    public PlayerCreatedGameMessage(User player) {
        super("PLAYER_CREATED");
        this.player = player;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }
}