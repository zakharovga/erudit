package com.erudit.messages;

import com.erudit.exceptions.*;

/**
 * Created by zakhar on 22.04.2016.
 */
public class ExceptionMessageFactory {

    private ExceptionMessageFactory() { }

    public static ExceptionMessage getMessage(GameException e) {
        Class<? extends GameException> c = e.getClass();
        if(c == FirstMoveException.class) {
            return new FirstMoveExceptionMessage("WRONG_FIRST_MOVE", e.getMessage());
        }
        if(c == IncorrectMoveException.class) {
            return new IncorrectMoveExceptionMessage("INCORRECT_MOVES", e.getMessage(),
                    ((IncorrectMoveException)e).getIncorrectMoves());
        }
        if(c == NoSuchWordException.class) {
            return new NoSuchWordExceptionMessage("NO_SUCH_WORD", e.getMessage(),
                    ((NoSuchWordException)e).getWord());
        }
        if(c == WordAlreadyUsedException.class) {
            return new WordAlreadyUsedExceptionMessage("WORD_ALREADY_USED", e.getMessage(),
                    ((WordAlreadyUsedException)e).getWord());
        }
        if(c == WordUsedTwiceException.class) {
            return new WordUsedTwiceExceptionMessage("WORD_USED_TWICE", e.getMessage(),
                    ((WordUsedTwiceException)e).getWord());
        }
        return null;
    }
}