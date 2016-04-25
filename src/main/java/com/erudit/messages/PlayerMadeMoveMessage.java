package com.erudit.messages;

import com.erudit.Game;
import com.erudit.Letter;
import com.erudit.Move;
import com.erudit.Player;

import java.util.List;
import java.util.Map;

/**
 * Created by zakharov_ga on 11.04.2016.
 */
public class PlayerMadeMoveMessage extends Message {

    private List<Move> moves;
    private Map<String, Integer> words;
    private List<Letter> letters;
    private String nextMove;

    public PlayerMadeMoveMessage(String nextMove, Player player, List<Move> moves, Map<String, Integer> words) {
        super("playerMadeMove");
        this.moves = moves;
        this.words = words;
        this.letters = player.getGivenLetters();
        this.nextMove = nextMove;
    }

    public String getNextMove() {
        return nextMove;
    }

    public void setNextMove(String nextMove) {
        this.nextMove = nextMove;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
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
}