package com.erudit.messages;

/**
 * Created by zakharov_ga on 13.04.2016.
 */
public class ExceptionMessage extends Message {

    private String message;

    public ExceptionMessage(String action, String message) {
        super(action);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}