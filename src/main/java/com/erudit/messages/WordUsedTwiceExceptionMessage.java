package com.erudit.messages;

/**
 * Created by zakhar on 22.04.2016.
 */
public class WordUsedTwiceExceptionMessage extends ExceptionMessage {

    private String word;

    public WordUsedTwiceExceptionMessage(String action, String message, String word) {
        super(action, message);
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}