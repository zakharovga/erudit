package com.erudit;

/**
 * Created by zakharov_ga on 12.04.2016.
 */
public class Move {

    private Letter letter;

    private int row;

    private int column;

    public Move() {
    }

    public Move(int row, int column, Letter letter) {
        this.row = row;
        this.column = column;
        this.letter = letter;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Letter getLetter() {
        return letter;
    }

    public void setLetter(Letter letter) {
        this.letter = letter;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (row != move.row) return false;
        if (column != move.column) return false;
        return letter.equals(move.letter);

    }


    @Override
    public int hashCode() {
        int result = letter.hashCode();
        result = 31 * result + row;
        result = 31 * result + column;
        return result;
    }

    @Override
    public String toString() {
        return "Move{" +
                "letter=" + letter +
                ", row=" + row +
                ", column=" + column +
                '}';
    }
}