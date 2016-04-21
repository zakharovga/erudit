package com.erudit;

/**
 * Created by zakharov_ga on 12.04.2016.
 */
public class Letter {
    private char letter;
    private int value;

    public Letter() {
    }

    public Letter(char letter, int value) {
        this.letter = letter;
        this.value = value;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Letter letter1 = (Letter) o;

        return letter == letter1.letter;

    }

    @Override
    public int hashCode() {
        return (int) letter;
    }

    @Override
    public String toString() {
        return "Letter{" +
                "letter=" + letter +
                ", value=" + value +
                '}';
    }
}