package com.erudit.messages;

/**
 * Created by zakharov_ga on 18.03.2016.
 */
public class RedirectMessage extends Message {

    private long gameId;

    public RedirectMessage(long gameId) {
        super("playerRedirected");
        this.gameId = gameId;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}