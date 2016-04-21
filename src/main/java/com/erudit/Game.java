package com.erudit;

import com.erudit.messages.GameOverMessage;
import com.erudit.messages.TimeOverMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erudit.messages.Message;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zakhar on 06.04.2016.
 */
public class Game {

    public static Set<String> dictionary = new HashSet<>();

    private static final AtomicLong gameIdSequence = new AtomicLong(1L);
    public static ObjectMapper mapper = new ObjectMapper();
    private static Map<String, String> DICTIONARY = new HashMap<>();

    private final long gameId;
    private volatile GameStatus gameStatus;
    private String creator;
    private final Map<Session, Player> sessions = new ConcurrentHashMap<>();
    private EruditGame eruditGame;
    private final Map<String, Player> httpSessions = new ConcurrentHashMap<>();
    private final Timer timer = new Timer();

    private final Object lock = new Object();

    private Game(long gameId) {
        this.gameId = gameId;
    }

    public static Game queueGame() {
        long id = gameIdSequence.getAndIncrement();
        Game game = new Game(id);
        game.setGameStatus(GameStatus.PENDING);
        StartEndpoint.addPendingGame(id, game);
        return game;
    }

    public static void addWord(String word, String description) {
        DICTIONARY.put(word, description);
    }

    public static void addWord(String word) {
        DICTIONARY.put(word, null);
    }

    public static boolean checkWord(String word) {
        return DICTIONARY.containsKey(word.toLowerCase());
    }

    public List<Player> getPlayers() {
        return eruditGame.getPlayers();
    }

    public void gameOver() {
        List<PlayerResult> gameResult = getGameResult();
        Message message = new GameOverMessage(gameResult);

        sendJsonMessage(message);
        System.out.println("GAME FINISHED");
    }

    private List<PlayerResult> getGameResult() {

        List<Player> players = new ArrayList<>(getPlayers());
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                if(p1.getTotalPoints() < p2.getTotalPoints())
                    return 1;
                if(p1.getTotalPoints() > p2.getTotalPoints())
                    return -1;
                return 0;
            }
        });

        List<PlayerResult> result = new ArrayList<>();
        for(Player player : players) {
            result.add(new PlayerResult(player.getUsername(), player.getTotalPoints()));
        }

        return result;
    }

    public void sendJsonMessage(Session session, Message message) {
        try {
            session.getBasicRemote().sendObject(message);
        }
        catch(IOException | EncodeException e) {
            handleException(e);
        }
    }

    public void sendJsonMessage(Message message) {

        for(Session session : getSessions().keySet()) {
            sendJsonMessage(session, message);
        }
    }

    public void sendJsonMessageToOpponents(Session playerSession, Message message) {

        for(Session session : getSessions().keySet()) {
            if(playerSession != session) {
                sendJsonMessage(session, message);
            }
        }
    }

    public void handleException(Throwable t) {
        t.printStackTrace();
    }

    public long getGameId() {
        return gameId;
    }

    public Map<Session, Player> getSessions() {
        return sessions;
    }

    public int size() {
        return sessions.size();
    }

    public void addSession(Session session, Player player) {
        sessions.put(session, player);
    }

    public void addHttpSession(String httpSessionId, Player player) {
        httpSessions.put(httpSessionId, player);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

//    public Session removeSession(String username) {
//        synchronized (sessions) {
//            Session oldSession = null;
//            Iterator<Map.Entry<Session, Player>> it = sessions.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry<Session, Player> entry = it.next();
//                if (entry.getValue().getUser().getUsername().equals(username)) {
//                    oldSession = entry.getKey();
//                    it.remove();
//                    break;
//                }
//            }
//            return oldSession;
//        }
//    }

    public Session getSession(String username) {
        Session session = null;
        for (Map.Entry<Session, Player> entry : sessions.entrySet()) {
            if (entry.getValue().getUsername().equals(username)) {
                session = entry.getKey();
                break;
            }
        }
        return session;
    }

    public List<User> getOpponents(Session session) {
        Player player = sessions.get(session);
        List<User> opponents = new ArrayList<>();
        for (Map.Entry<Session, Player> entry : sessions.entrySet()) {
            User opponent = entry.getValue().getUser();
            if (!player.getUsername().equalsIgnoreCase(opponent.getUsername()))
                opponents.add(opponent);
        }
        return opponents;
    }

//    public Map<Session, List<Player>> getOpponents(Session session) {
//        Map<Session, List<Player>> sessionOpponentsMap = new HashMap<>();
//        Collection<Player> players = sessions.values();
//        for (Player player : players) {
//            Collection<Player> temp = new
//            if (!player.getUsername().equalsIgnoreCase(player.getUser().getUsername()))
//                opponents.add(opponent.getUser());
//        }
//    }

    public int getNumberOfPlayers() {
        return sessions.size();
    }

    public boolean checkReadyPlayers() {
        if (getNumberOfPlayers() < 2)
            return false;
        for (Player player : sessions.values()) {
            if (player.getPlayerStatus() != PlayerStatus.READY)
                return false;
        }
        return true;
    }

    public boolean checkActivePlayers() {
        return eruditGame.checkActivePlayers();
    }

    public synchronized boolean setActiveAndCheck(Session session, Player player) {
        setPlayerStatus(player, PlayerStatus.ACTIVE);
        addSession(session, player);
        return checkActivePlayers();
    }

    public boolean checkTurnAndReset(Session session) {
        synchronized(lock) {
            Player player = sessions.get(session);
            Player nextMove = getNextMove();
            if (player == nextMove) {
                nextMove();
                timer.start();
            }
            return player == nextMove;
        }
    }

    public boolean skipTurn(Player player) {
        return eruditGame.skipTurn(player);
    }

    private void resetSkippedTurns() {
        eruditGame.resetSkippedTurns();
    }

    public List<Letter> changeLetters(Player player, List<Letter> letters) {
        List<Letter> result = eruditGame.changeLetters(player, letters);
//        nextMove();
        timer.start();
        return result;
    }

    public Map<String, Integer> computeMove(List<Move> moves, Player player) throws Exception {
        synchronized(lock) {
            Map<String, Integer> result = eruditGame.computeMove(moves, player);
            resetSkippedTurns();
//            nextMove();
            timer.start();
            return result;
        }
    }

    public void setPlayerStatus(Player player, PlayerStatus playerStatus) {
        player.setPlayerStatus(playerStatus);
    }

    public void setRedirectingPlayers() {
        for (Player player : sessions.values()) {
            player.setPlayerStatus(PlayerStatus.REDIRECTING);
        }
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

//    public boolean checkHttpSessionId(String httpSessionId) {
//        for (Player player : eruditGame.getPlayers()) {
//            if (player.getHttpSessionId().equals(httpSessionId))
//                if (player.getPlayerStatus() == PlayerStatus.READY) {
//                    player.setPlayerStatus(PlayerStatus.REDIRECTING);
//                    return true;
//                }
//        }
//        return false;
//    }

    public boolean checkHttpSessionId(String httpSessionId) {
        return httpSessions.get(httpSessionId) != null;
    }

//    public Player getPlayerByHttpSessionId(String httpSessionId) {
//        for (Player player : eruditGame.getPlayers()) {
//            if (player.getHttpSessionId().equals(httpSessionId))
//                return player;
//        }
//        return null;
//    }

    public Player getPlayerByHttpSessionId(String httpSessionId) {
        return httpSessions.get(httpSessionId);
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId=" + gameId +
                ", creator='" + creator + '\'' +
                '}';
    }

    public void initPlayers() {
        eruditGame.setPlayers(sessions.values());
    }

    public Player nextMove() {
        return eruditGame.nextMove();
    }

    public void start() {
        eruditGame.start();
        setGameStatus(GameStatus.ACTIVE);
        timer.start();
    }

    public Player getPlayer(Session session) {
        return sessions.get(session);
    }

    public Player getPlayer(String username) {
        for(Player player : sessions.values()) {
            if(username.equals(player.getUsername()))
                return player;
        }
        return null;
    }

    public Player getNextMove() {
        return eruditGame.getNextMove();
    }

    public void prepare() {
        eruditGame = new EruditGame();
        eruditGame.setPlayers(sessions.values());

        setGameStatus(GameStatus.REDIRECTING);
    }

    public static class PlayerResult {
        String player;
        int points;

        public PlayerResult(String player, int points) {
            this.player = player;
            this.points = points;
        }

        public String getPlayer() {
            return player;
        }

        public void setPlayer(String player) {
            this.player = player;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }

    /**
     * Created by zakharov_ga on 25.03.2016.
     */
    private class Timer {

        private final static int INTERVAL = 60;
        private ScheduledExecutorService timer= Executors.newScheduledThreadPool(1);
        private ScheduledFuture scheduledFuture;
        private Task task;

        public void start() {
            synchronized(lock) {
                if(task != null)
                    task.deactivate();
                if(scheduledFuture != null)
                    scheduledFuture.cancel(false);
                task = new Task();
                scheduledFuture = timer.scheduleAtFixedRate(task, INTERVAL, INTERVAL, TimeUnit.SECONDS);
            }
        }

        private class Task implements Runnable {

            private volatile boolean isActive = true;

            @Override
            public void run() {
                synchronized(lock) {
                    if (isActive) {
                        Player player = getNextMove();

                        if (skipTurn(player)) {
                            gameOver();
                        }
                        else {
                            String playerName = player.getUsername();
                            String nextMove = nextMove().getUsername();

                            sendJsonMessage(new TimeOverMessage(playerName, nextMove));
                        }
                    }
                }
            }

            public void deactivate() {
                isActive = false;
            }
        }
    }
}