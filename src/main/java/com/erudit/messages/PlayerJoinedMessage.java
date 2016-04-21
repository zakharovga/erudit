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

    private List<Opponent> opponents = new ArrayList<>();

    public PlayerJoinedMessage(Game game, Player player) {
        super("playerJoined");
        for (Player opponent : game.getSessions().values()) {
            if (!opponent.getUser().getUsername().equalsIgnoreCase(player.getUser().getUsername())) {
                opponents.add(new Opponent(opponent.getUser(), opponent.getPlayerStatus() == PlayerStatus.READY));
            }
        }
    }

    public List<Opponent> getOpponents() {
        return opponents;
    }

    private static class Opponent {

        public Opponent(User user, boolean ready) {
            this.ready = ready;
            this.user = user;
        }

        User user;
        boolean ready;

        public boolean isReady() {
            return ready;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
}