package com.erudit.messages;

import com.erudit.Game;
import com.erudit.Letter;

import java.util.List;

/**
 * Created by zakharov_ga on 24.03.2016.
 */
public class PlayerChangedLettersMessage extends Message {

    private List<Letter> changedLetters;

    private String nextMove;

    public PlayerChangedLettersMessage(String nextMove, List<Letter> changedLetters) {
        super("playerChangedLetters");
        this.changedLetters = changedLetters;
        this.nextMove = nextMove;
    }

    public List<Letter> getChangedLetters() {
        return changedLetters;
    }

    public void setChangedLetters(List<Letter> changedLetters) {
        this.changedLetters = changedLetters;
    }

    public String getNextMove() {
        return nextMove;
    }

    public void setNextMove(String nextMove) {
        this.nextMove = nextMove;
    }
}