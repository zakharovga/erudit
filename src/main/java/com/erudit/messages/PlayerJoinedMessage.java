package com.erudit.messages;

import com.erudit.Game;
import com.erudit.Player;
import com.erudit.PlayerStatus;
import com.erudit.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zakharov_ga on 02.03.2016.
 */
public class PlayerJoinedMessage extends Message {

    private User player;
    private List<Game.Opponent> opponents = new ArrayList<>();

    public PlayerJoinedMessage(User player, List<Game.Opponent> opponents) {
        super("PLAYER_JOINED");
        this.player = player;
        this.opponents = opponents;
    }

    public List<Game.Opponent> getOpponents() {
        return opponents;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }
}