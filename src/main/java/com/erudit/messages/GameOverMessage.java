package com.erudit.messages;

import com.erudit.Game;

import java.util.List;
import java.util.Map;

/**
 * Created by zakhar on 20.04.2016.
 */
public class GameOverMessage extends Message {

    private List<Game.PlayerResult> gameResult;

    public GameOverMessage(List<Game.PlayerResult> gameResult) {
        super("GAME_OVER");
        this.gameResult = gameResult;
    }

    public List<Game.PlayerResult> getGameResult() {
        return gameResult;
    }

    public void setGameResult(List<Game.PlayerResult> gameResult) {
        this.gameResult = gameResult;
    }
}