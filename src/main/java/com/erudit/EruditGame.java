package com.erudit;

import com.erudit.exceptions.*;

import java.util.*;

/**
 * Created by zakharov_ga on 30.12.2015.
 */
public class EruditGame {

    private final static Map<String, String> DICTIONARY = new HashMap<>();
    private List<Player> players;
    private final Letter[][] board = new Letter[15][15];
    private final Map<Letter, Integer> letterContainer = new HashMap<>();
    private int letterQuantity;
    private Player nextMove;
    private Set<String> usedWords;
    private Set<Move> madeMoves;
    private Map<Player, Integer> skippedTurns;

    public static void addWord(String word, String description) {
        DICTIONARY.put(word, description);
    }

    public static void addWord(String word) {
        DICTIONARY.put(word, null);
    }

    public static boolean checkWord(String word) {
        return DICTIONARY.containsKey(word.toLowerCase());
    }

    // увеличивает счетчик пропущенных ходов
    // вызывается при пропуске хода (замене букв)
    // возвращает true - если все игроки пропустили ход по 2 раза, false - в противном случае
    public boolean skipTurn(Player player) {
        Integer n = skippedTurns.get(player);
        skippedTurns.put(player, ++n);
        for(Map.Entry<Player, Integer> entry : skippedTurns.entrySet()) {
            if(entry.getKey().getPlayerStatus() == PlayerStatus.ACTIVE)
                if(entry.getValue() < 2)
                    return false;
        }
        return true;
    }

    // сбрасывает счетчик пропущенных ходов
    // вызывается, когда игрок составил слово
    public void resetSkippedTurns() {
        for(Map.Entry<Player, Integer> entry : skippedTurns.entrySet()) {
            entry.setValue(0);
        }
    }

    //
    public Player nextMove() {
        int index;
        int n = players.size();
        if (nextMove == null) {
            index = (int) (Math.random() * (n - 1));
        } else {
            index = players.indexOf(nextMove);
            index++;
        }
        while(true) {
            nextMove = players.get(index % n);
            if(nextMove.getPlayerStatus() == PlayerStatus.ACTIVE)
                return nextMove;
            else
                index++;
        }
    }

    public Player getNextMove() {
        return nextMove;
    }

    public Map<Letter, Integer> getLetterContainer() {
        return letterContainer;
    }

    public int getLetterQuantity() {
        return letterQuantity;
    }

    public EruditGame() {
        this.initLetterContainer();
    }

    private void initPlayerLetters() {
        for (Player player : players) {
            giveLettersToPlayer(player, 7);
        }
    }

    public Map<String, Integer> computeMove(List<Move> moves, Player player) throws FirstMoveException,
            IncorrectMoveException, WordAlreadyUsedException, NoSuchWordException, WordUsedTwiceException {

        boolean isFirstMove = false;
        if(board[7][7] == null)
            isFirstMove = true;

        madeMoves = new HashSet<>();

        for (Move move : moves) {
            madeMoves.add(move);
            int row = move.getRow();
            int column = move.getColumn();
            Letter letter = move.getLetter();
            board[row][column] = letter;
        }

        defineCorrectness(isFirstMove);

        Set<Word> boardWords = new HashSet<>();
        Set<String> words = new HashSet<>();

        for (Move move : moves) {
            Word verticalWord = getVerticalWord(move);
            if (verticalWord != null) {
                if (boardWords.add(verticalWord)) {
                    if (words.add(verticalWord.getWord())) {
                        if(usedWords.contains(verticalWord.getWord())) {
                            throw new WordAlreadyUsedException(verticalWord.getWord());
                        }
                        else {
                            if(!EruditGame.checkWord(verticalWord.getWord()))
                                throw new NoSuchWordException(verticalWord.getWord());
                        }
                    }
                    else
                        throw new WordUsedTwiceException(verticalWord.getWord());
                }
            }

            Word horizontalWord = getHorizontalWord(move);
            if (horizontalWord != null) {
                if (boardWords.add(horizontalWord)) {
                    if (words.add(horizontalWord.getWord())) {
                        if(usedWords.contains(horizontalWord.getWord())) {
                            throw new WordAlreadyUsedException(horizontalWord.getWord());
                        }
                        else {
                            if(!EruditGame.checkWord(horizontalWord.getWord()))
                                throw new NoSuchWordException(horizontalWord.getWord());
                        }
                    }
                    else
                        throw new WordUsedTwiceException(horizontalWord.getWord());
                }
            }
        }

        Map<String, Integer> result = new HashMap<>();
        for (Word word : boardWords) {
            String stringWord = word.getWord();
            usedWords.add(stringWord);
            word.computePoints();
            int points = word.getPoints();
            player.addPoints(points);
            result.put(stringWord, points);
        }

        giveLettersToPlayer(player, moves.size());

        return result;
    }

    private Word getVerticalWord(Move move) {
        int row = move.getRow();
        int column = move.getColumn();
        while (row > 0) {
            row--;
            if (board[row][column] == null) {
                row++;
                break;
            }
        }
        int begin = row;
        Word word = new Word();
        StringBuilder sb = new StringBuilder();

        Letter letter = board[begin][column];
        word.addMove(new Move(row, column, letter));

        sb.append(letter.getLetter());
        while (row < 14) {
            row++;
            if (board[row][column] == null) {
                row--;
                break;
            } else {
                letter = board[row][column];
                word.addMove(new Move(row, column, letter));
                sb.append(board[row][column].getLetter());
            }
        }
        int end = row;
        if (begin == end)
            return null;
        else {
            word.setWord(sb.toString());
            word.setBeginRow(begin);
            word.setBeginColumn(column);
            word.setEndRow(end);
            word.setEndColumn(column);

            return word;
        }
    }

    private Word getHorizontalWord(Move move) {
        int column = move.getColumn();
        int row = move.getRow();
        while (column > 0) {
            column--;
            if (board[row][column] == null) {
                column++;
                break;
            }
        }
        int begin = column;
        Word word = new Word();
        StringBuilder sb = new StringBuilder();

        Letter letter = board[row][begin];
        word.addMove(new Move(row, begin, letter));

        sb.append(board[row][begin].getLetter());
        while (column < 14) {
            column++;
            if (board[row][column] == null) {
                column--;
                break;
            } else {
                letter = board[row][column];
                word.addMove(new Move(row, column, letter));
                sb.append(board[row][column].getLetter());
            }
        }
        int end = column;
        if (begin == end)
            return null;
        else {
            word.setWord(sb.toString());
            word.setBeginRow(row);
            word.setBeginColumn(begin);
            word.setEndRow(row);
            word.setEndColumn(end);

            return word;
        }
    }

    private void defineCorrectness(boolean isFirstMove) throws FirstMoveException, IncorrectMoveException {
        Set<Move> shallowCopy = new HashSet<>(madeMoves);
        Set<Move> correctMoves = new HashSet<>();

        if(isFirstMove){
            if(board[7][7] == null)
                throw new FirstMoveException();
            else {
                Move firstMove = new Move(7, 7, board[7][7]);
                shallowCopy.remove(firstMove);
                correctMoves.add(firstMove);
            }
        }

        while (shallowCopy.size() != 0) {
            Iterator<Move> iterator = shallowCopy.iterator();
            boolean correct = false;
            while (iterator.hasNext()) {
                Move move = iterator.next();
                int row = move.getRow();
                int column = move.getColumn();
                int topRow = row - 1;
                int bottomRow = row + 1;
                int leftColumn = column - 1;
                int rightColumn = column + 1;

                if(topRow>=0) {
                    if (board[topRow][column] != null) {
                        Move topMove = new Move(topRow, column, board[topRow][column]);
                        if (!madeMoves.contains(topMove) || correctMoves.contains(topMove)) {
                            iterator.remove();
                            correctMoves.add(move);
                            correct = true;
                            continue;
                        }
                    }
                }
                if(bottomRow <= 14) {
                    if (board[bottomRow][column] != null) {
                        Move bottomMove = new Move(bottomRow, column, board[bottomRow][column]);
                        if (!madeMoves.contains(bottomMove) || correctMoves.contains(bottomMove)) {
                            iterator.remove();
                            correctMoves.add(move);
                            correct = true;
                            continue;
                        }
                    }
                }
                if(leftColumn >= 0) {
                    if (board[row][leftColumn] != null) {
                        Move leftMove = new Move(row, leftColumn, board[row][leftColumn]);
                        if (!madeMoves.contains(leftMove) || correctMoves.contains(leftMove)) {
                            iterator.remove();
                            correctMoves.add(move);
                            correct = true;
                            continue;
                        }
                    }
                }
                if(rightColumn <= 14) {
                    if (board[row][rightColumn] != null) {
                        Move rightMove = new Move(row, rightColumn, board[row][rightColumn]);
                        if (!madeMoves.contains(rightMove) || correctMoves.contains(rightMove)) {
                            iterator.remove();
                            correctMoves.add(move);
                            correct = true;
                            continue;
                        }
                    }
                }
            }
            if(!correct)
                throw new IncorrectMoveException(shallowCopy);
        }
    }

    private List<Letter> giveLettersToPlayer(Player player, int n) {
        List<Letter> givenLetters = new ArrayList<Letter>();
        for (int i = 0; i < n; i++) {
            int p = (int) (Math.random() * letterQuantity) + 1;
            int sum = 0;
            Iterator<Map.Entry<Letter, Integer>> iterator = letterContainer.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Letter, Integer> entry = iterator.next();           //!!!!!!!!!!!!!!!!! ConcurrentModificationException
                sum += entry.getValue();
                if (sum >= p) {
                    Letter letter = entry.getKey();
                    player.addLetter(letter);
                    givenLetters.add(letter);
                    Integer quantity;
                    if ((quantity = entry.getValue()) == 1) {
                        iterator.remove();
                    } else
                        entry.setValue(--quantity);
                    break;
                }
            }
            letterQuantity--;
        }
        player.setGivenLetters(givenLetters);
        return givenLetters;
    }

    public List<Letter> changeLetters(Player player, List<Letter> letters) {

        addLettersToContainer(letters);
        player.removeLetters(letters);

        return giveLettersToPlayer(player, letters.size());
    }

    private void addLettersToContainer(List<Letter> letters) {
        for (Letter letter : letters)
            addLetterToContainer(letter);
    }

    private void addLetterToContainer(Letter letter) {
        Integer quantity;
        if ((quantity = letterContainer.get(letter)) == null)
            letterContainer.put(letter, 1);
        else
            letterContainer.put(letter, ++quantity);
    }

    private void initSkippedTurns() {
        skippedTurns = new HashMap<>();
        for(Player player : players) {
            skippedTurns.put(player, 0);
        }
    }

    public void start() {
        initSkippedTurns();
        initPlayerLetters();
        nextMove();
        usedWords = new HashSet<>();
    }

    public void setPlayers(Collection<Player> players) {
        this.players = new ArrayList<>(players);
    }

    private void initLetterContainer() {
        letterContainer.put(new Letter('А', 1), 10);
        letterContainer.put(new Letter('Б', 3), 3);
        letterContainer.put(new Letter('В', 2), 5);
        letterContainer.put(new Letter('Г', 3), 3);
        letterContainer.put(new Letter('Д', 2), 5);
        letterContainer.put(new Letter('Е', 1), 9);
        letterContainer.put(new Letter('Ж', 5), 2);
        letterContainer.put(new Letter('З', 5), 2);
        letterContainer.put(new Letter('И', 1), 8);
        letterContainer.put(new Letter('Й', 2), 4);
        letterContainer.put(new Letter('К', 2), 6);
        letterContainer.put(new Letter('Л', 2), 4);
        letterContainer.put(new Letter('М', 2), 5);
        letterContainer.put(new Letter('Н', 1), 8);
        letterContainer.put(new Letter('О', 1), 10);
        letterContainer.put(new Letter('П', 2), 6);
        letterContainer.put(new Letter('Р', 2), 6);
        letterContainer.put(new Letter('С', 2), 6);
        letterContainer.put(new Letter('Т', 2), 5);
        letterContainer.put(new Letter('У', 3), 3);
        letterContainer.put(new Letter('Ф', 10), 1);
        letterContainer.put(new Letter('Х', 5), 2);
        letterContainer.put(new Letter('Ц', 10), 1);
        letterContainer.put(new Letter('Ч', 5), 2);
        letterContainer.put(new Letter('Ш', 10), 1);
        letterContainer.put(new Letter('Щ', 10), 1);
        letterContainer.put(new Letter('Ъ', 10), 1);
        letterContainer.put(new Letter('Ы', 5), 2);
        letterContainer.put(new Letter('Ь', 5), 2);
        letterContainer.put(new Letter('Э', 10), 1);
        letterContainer.put(new Letter('Ю', 10), 1);
        letterContainer.put(new Letter('Я', 3), 3);

        for (Letter letter : letterContainer.keySet()) {
            letterQuantity += letterContainer.get(letter);
        }
    }

    public boolean checkActivePlayers() {
        for(Player player : players) {
            if(player.getPlayerStatus() != PlayerStatus.ACTIVE)
                return false;
        }
        return true;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void cancelMoves() {
        for(Move move : madeMoves) {
            int row = move.getRow();
            int column = move.getColumn();
            board[row][column] = null;
        }
    }

    public static class Word {
        private int beginRow;
        private int beginColumn;
        private int endRow;
        private int endColumn;
        private String word;
        private int points = 0;
        private Set<Move> moves = new HashSet<>();

        public Word() { }

        public int computePoints() {
            int k = 1;
            int points = 0;
            for (Move move : moves) {
                int row = move.getRow();
                int column = move.getColumn();
                if ((row == 1 && column == 1) || (row == 1 && column == 13) || (row == 2 && column == 2) ||
                        (row == 2 && column == 12) || (row == 3 && column == 3) || (row == 3 && column == 11) ||
                        (row == 4 && column == 4) || (row == 4 && column == 10) || (row == 10 && column == 4) ||
                        (row == 10 && column == 10) || (row == 13 && column == 3) || (row == 11 && column == 11) ||
                        (row == 12 && column == 2) || (row == 12 && column == 12) || (row == 13 && column == 1) ||
                        (row == 13 && column == 13)) {
                    points += move.getLetter().getValue();
                    k *= 2;
                } else if ((row == 0 && column == 0) || (row == 0 && column == 7) || (row == 0 && column == 14) ||
                        (row == 7 && column == 0) || (row == 7 && column == 14) || (row == 14 && column == 0) ||
                        (row == 14 && column == 7) || (row == 14 && column == 14)) {
                    points += move.getLetter().getValue();
                    k *= 3;
                } else if ((row == 0 && column == 3) || (row == 0 && column == 11) || (row == 2 && column == 6) ||
                        (row == 2 && column == 8) || (row == 3 && column == 0) || (row == 3 && column == 7) ||
                        (row == 3 && column == 14) || (row == 6 && column == 2) || (row == 6 && column == 6) ||
                        (row == 6 && column == 8) || (row == 6 && column == 12) || (row == 7 && column == 3) ||
                        (row == 7 && column == 11) || (row == 8 && column == 2) || (row == 8 && column == 6) ||
                        (row == 8 && column == 8) || (row == 8 && column == 12) || (row == 11 && column == 0) ||
                        (row == 11 && column == 7) || (row == 11 && column == 14) || (row == 12 && column == 6) ||
                        (row == 12 && column == 8) || (row == 14 && column == 3) || (row == 14 && column == 11)) {
                    points += move.getLetter().getValue() * 2;
                } else if ((row == 1 && column == 5) || (row == 1 && column == 9) || (row == 5 && column == 1) ||
                        (row == 5 && column == 5) || (row == 5 && column == 9) || (row == 5 && column == 13) ||
                        (row == 9 && column == 1) || (row == 9 && column == 5) || (row == 9 && column == 9) ||
                        (row == 9 && column == 13) || (row == 13 && column == 5) || (row == 13 && column == 9)) {
                    points += move.getLetter().getValue() * 3;
                } else
                    points += move.getLetter().getValue();
            }
            points *= k;
            this.points = points;
            return points;
        }

        public int getBeginRow() {
            return beginRow;
        }

        public void setBeginRow(int beginRow) {
            this.beginRow = beginRow;
        }

        public int getBeginColumn() {
            return beginColumn;
        }

        public void setBeginColumn(int beginColumn) {
            this.beginColumn = beginColumn;
        }

        public int getEndRow() {
            return endRow;
        }

        public void setEndRow(int endRow) {
            this.endRow = endRow;
        }

        public int getEndColumn() {
            return endColumn;
        }

        public void setEndColumn(int endColumn) {
            this.endColumn = endColumn;
        }

        public void addMove(Move move) {
            this.moves.add(move);
        }

        public int getPoints() {
            return points;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Word word1 = (Word) o;

            if (beginRow != word1.beginRow) return false;
            if (beginColumn != word1.beginColumn) return false;
            if (endRow != word1.endRow) return false;
            if (endColumn != word1.endColumn) return false;
            return word.equals(word1.word);
        }

        @Override
        public int hashCode() {
            int result = beginRow;
            result = 31 * result + beginColumn;
            result = 31 * result + endRow;
            result = 31 * result + endColumn;
            result = 31 * result + word.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Word{" +
                    "beginRow=" + beginRow +
                    ", beginColumn=" + beginColumn +
                    ", endRow=" + endRow +
                    ", endColumn=" + endColumn +
                    ", word='" + word + '\'' +
                    ", points=" + points +
                    ", moves=" + moves +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "EruditGame{" +
                "players=" + players +
                '}';
    }
}