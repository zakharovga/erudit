package com.erudit.messages;

import com.erudit.Game;
import com.erudit.Player;
import com.erudit.PlayerStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zakharov_ga on 02.03.2016.
 */
public class PlayerJoinedMessage extends Message {

    private List<Game.Opponent> opponents = new ArrayList<>();

    public PlayerJoinedMessage(List<Game.Opponent> opponents) {
        super("playerJoined");
        this.opponents = opponents;
    }

    public List<Game.Opponent> getOpponents() {
        return opponents;
    }
}