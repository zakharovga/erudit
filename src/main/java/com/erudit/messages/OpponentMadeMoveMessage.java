package com.erudit.messages;

import com.erudit.Game;
import com.erudit.Move;
import com.erudit.Player;

import java.util.List;
import java.util.Map;

/**
 * Created by zakharov_ga on 22.03.2016.
 */
public class OpponentMadeMoveMessage extends Message {

    private String nextMove;

    private String previousMove;

    private List<Move> moves;

    private Map<String, Integer> words;

    public OpponentMadeMoveMessage(String nextMove, Player player, List<Move> moves, Map<String, Integer> words) {
        super("opponentMadeMove");
        this.nextMove = nextMove;
        this.moves = moves;
        this.previousMove = player.getUser().getUsername();
        this.words = words;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public String getNextMove() {
        return nextMove;
    }

    public void setNextMove(String nextMove) {
        this.nextMove = nextMove;
    }

    public String getPreviousMove() {
        return previousMove;
    }

    public void setPreviousMove(String previousMove) {
        this.previousMove = previousMove;
    }

    public Map<String, Integer> getWords() {
        return words;
    }

    public void setWords(Map<String, Integer> words) {
        this.words = words;
    }
}
