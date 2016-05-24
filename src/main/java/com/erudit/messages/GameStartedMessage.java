package com.erudit.messages;

import com.erudit.Game;
import com.erudit.Letter;
import com.erudit.Player;
import com.erudit.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zakharov_ga on 02.03.2016.
 */
public class GameStartedMessage extends Message {

    private String nextMove;

    private List<Letter> givenLetters;

    private User player;

    private List<User> opponents = new ArrayList<>();

    public String getNextMove() {
        return nextMove;
    }

    public void setNextMove(String nextMove) {
        this.nextMove = nextMove;
    }

    public List<Letter> getGivenLetters() {
        return givenLetters;
    }

    public void setGivenLetters(List<Letter> givenLetters) {
        this.givenLetters = givenLetters;
    }

    public GameStartedMessage(String nextMove, Player player, List<User> opponents) {
        super("GAME_STARTED");
        this.nextMove = nextMove;
        this.givenLetters = player.getGivenLetters();
        this.player = player.getUser();
        this.opponents = opponents;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public List<User> getOpponents() {
        return opponents;
    }
}