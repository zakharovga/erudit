package com.erudit.messages;

/**
 * Created by zakharov_ga on 02.03.2016.
 */
public abstract class Message {

    private String action;

    public Message() { }

    public Message(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }
}