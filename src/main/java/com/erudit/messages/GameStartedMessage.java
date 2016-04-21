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

    public GameStartedMessage(Game game, Player player, List<User> opponents) {
        super("gameStarted");
        this.nextMove = game.getNextMove().getUsername();
        this.givenLetters = player.getGivenLetters();
        this.opponents = opponents;
    }

    public List<User> getOpponents() {
        return opponents;
    }
}