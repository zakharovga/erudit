package com.erudit;

import java.io.Serializable;

/**
 * Created by zakharov_ga on 30.12.2015.
 */
public class User implements Serializable {

    public static int DEFAULT_RATING = 1600;

    private long id;
    private String email;
    private String username;
    private int raiting;
    private boolean guest;

    public User() { }

    public User(String email, String username, int raiting, boolean guest) {
        this.email = email;
        this.username = username;
        this.raiting = raiting;
        this.guest = guest;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRaiting() {
        return raiting;
    }

    public void setRaiting(int raiting) {
        this.raiting = raiting;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
}