package com.erudit;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zakharov_ga on 07.04.2016.
 */
public class Player {

    private final User user;
    private Map<Letter, Integer> playerLetters;
    private List<Letter> givenLetters;
    private int totalPoints;
    private int lastPoints;
    private PlayerStatus playerStatus;

    public Player(User user) {
        this.user = user;
        this.playerLetters = new HashMap<>();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public List<Letter> getGivenLetters() {
        return givenLetters;
    }

    public void setGivenLetters(List<Letter> givenLetters) {
        this.givenLetters = givenLetters;
    }

    public User getUser() {
        return user;
    }

    public void addPoints(int points) {
        lastPoints = points;
        totalPoints += points;
    }

    public Map<Letter, Integer> getPlayerLetters() {
        return playerLetters;
    }

    public void addLetters(Collection<Letter> letters) {
        for (Letter letter : letters)
            addLetter(letter);
    }

    public void addLetter(Letter letter) {
        Integer quantity;
        if ((quantity = playerLetters.get(letter)) == null)
            playerLetters.put(letter, 1);
        else
            playerLetters.put(letter, ++quantity);
    }

    public void removeLetters(List<Letter> letters) {
        for (Letter letter : letters)
            removeLetter(letter);
    }

    public void removeLetter(Letter letter) {
        Integer quantity;
        if ((quantity = playerLetters.get(letter)) == 1)
            playerLetters.remove(letter);
        else
            playerLetters.put(letter, --quantity);
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    @Override
    public String toString() {
        return "Player{" +
                "user=" + user +
                '}';
    }
}
