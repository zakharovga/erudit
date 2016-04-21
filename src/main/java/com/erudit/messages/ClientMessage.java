package com.erudit.messages;

import com.erudit.Letter;
import com.erudit.Move;

import java.util.List;
import java.util.Map;

/**
 * Created by zakharov_ga on 10.03.2016.
 */
public class ClientMessage extends Message {

    private String nextMove;

    private List<Move> move;

    private List<Letter> letters;

    private Map<String, Integer> words;

    private String readyOpponent;

    private boolean ready;

    public ClientMessage() {}

    public String getNextMove() {
        return nextMove;
    }

    public void setNextMove(String nextMove) {
        this.nextMove = nextMove;
    }

    public List<Move> getMove() {
        return move;
    }

    public void setMove(List<Move> move) {
        this.move = move;
    }

    public List<Letter> getLetters() {
        return letters;
    }

    public void setLetters(List<Letter> letters) {
        this.letters = letters;
    }

    public Map<String, Integer> getWords() {
        return words;
    }

    public void setWords(Map<String, Integer> words) {
        this.words = words;
    }

    public String getReadyOpponent() {
        return readyOpponent;
    }

    public void setReadyOpponent(String readyOpponent) {
        this.readyOpponent = readyOpponent;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}