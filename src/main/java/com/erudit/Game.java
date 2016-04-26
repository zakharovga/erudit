package com.erudit;

import com.erudit.exceptions.GameException;
import com.erudit.messages.*;

import javax.websocket.CloseReason;
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

    private static final AtomicLong gameIdSequence = new AtomicLong(1L);

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

    public void closeSession(Session session, CloseReason closeReason) {
        synchronized(lock) {
            if(session.isOpen()) {
                try {
                    session.close(closeReason);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void joinPlayer(Session session, String httpSessionId, User user) {
        String username = user.getUsername();

        synchronized (lock) {
            if (getGameStatus() == GameStatus.CLOSED) {
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                            "Эта игра больше не существует"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (getGameStatus() != GameStatus.PENDING) {
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                            "Эта игра уже началась"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (size() > 3) {
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                            "В этой игре уже максимум игроков"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (getSession(username) != null) {
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                            "Вы уже присоединились к этой игре"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Game oldGame = GameEndpoint.getGame(username);
                if (oldGame != null) {
                    Session oldSession = oldGame.getSession(username);
                    closeSession(oldSession, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                            "Соединение разорвано, т.к. Вы начали новую игру"));
                }

                Player joiner = new Player(user);
                addSession(session, joiner);
                addHttpSession(httpSessionId, joiner);

                GameEndpoint.addUsername(username, this);
                GameEndpoint.addSession(session, this);


                List<Opponent> opponents = new ArrayList<>();

                for (Player opponent : getSessions().values()) {
                    if (!opponent.getUser().getUsername().equalsIgnoreCase(joiner.getUsername())) {
                        opponents.add(new Opponent(opponent.getUser(), opponent.getPlayerStatus() == PlayerStatus.READY));
                    }
                }

                PlayerJoinedMessage playerJoinedMessage = new PlayerJoinedMessage(opponents);

                sendJsonMessageToOpponents(session, new OpponentJoinedMessage(user));
                sendJsonMessage(session, playerJoinedMessage);
            }
        }
    }

    public void disconnectPlayer(Session session) {
        synchronized (lock) {
            String username = getPlayer(session).getUsername();

            removeSession(session);
            GameEndpoint.removeSession(session);

            if(getGameStatus() == GameStatus.REDIRECTING) {
                return;
            }
            if (size() == 0) {
                GameEndpoint.removeUsername(username);
                GameEndpoint.removeGame(gameId);
                if(getGameStatus() == GameStatus.PENDING) {
                    StartEndpoint.removePendingGame(getGameId());
                }
                else if(getGameStatus() == GameStatus.ACTIVE) {
                    GameEndpoint.removeActiveGame(gameId);
                }
                this.setGameStatus(GameStatus.CLOSED);
            } else
                sendJsonMessageToOpponents(session, new OpponentQuitMessage(username));
        }
    }

    public void processChangingLetters(Session session, List<Letter> changedLetters) {
        Player player = getPlayer(session);
        if (player == null)
            return;
        synchronized (lock) {
            if (checkTurn(session)) {
                if (skipTurn(player)) {
                    gameOver();
                }
                if (changedLetters != null && changedLetters.size() != 0) {
                    List<Letter> newLetters = changeLetters(player, changedLetters);

                    nextMove();
                    timer.start();
                    String nextMove = getNextMove().getUsername();

                    Message playerMessage = new PlayerChangedLettersMessage(nextMove, newLetters);
                    Message opponentMessage = new OpponentChangedLettersMessage(nextMove, player.getUser().getUsername());

                    sendJsonMessage(session, playerMessage);
                    sendJsonMessageToOpponents(session, opponentMessage);
                }
            }
        }
    }

    public void processMoves(Session session, List<Move> moves) {
        Player player = getPlayer(session);
        if (player == null)
            return;
        synchronized (lock) {
            if (checkTurn(session)) {
                try {
                    Map<String, Integer> words = computeMove(moves, player);

                    resetSkippedTurns();
                    nextMove();
                    timer.start();
                    String nextMove = getNextMove().getUsername();

                    Message playerMessage = new PlayerMadeMoveMessage(nextMove, player, moves, words);
                    Message opponentMessage = new OpponentMadeMoveMessage(nextMove, player, moves, words);

                    sendJsonMessage(session, playerMessage);
                    sendJsonMessageToOpponents(session, opponentMessage);
                } catch (GameException e) {
                    Message message = ExceptionMessageFactory.getMessage(e);
                    sendJsonMessage(session, message);
                }
            }
        }
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
            if(session.isOpen())
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
        synchronized(lock) {
            Session session = null;
            for (Map.Entry<Session, Player> entry : sessions.entrySet()) {
                if (entry.getValue().getUsername().equals(username)) {
                    session = entry.getKey();
                    break;
                }
            }
            return session;
        }
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

    public void setReadyPlayerAndCheck(Session session, Player player, boolean ready) {
        synchronized(lock) {
            if (ready)
                setPlayerStatus(player, PlayerStatus.READY);
            else
                setPlayerStatus(player, PlayerStatus.NOT_READY);

            if (size() < 2)
                return;
            boolean allReady = true;
            for (Player each : sessions.values()) {
                if (each.getPlayerStatus() != PlayerStatus.READY)
                    allReady = false;
            }
            if (allReady)
                redirect();
            else
                sendJsonMessageToOpponents(session, new OpponentReadyMessage(player.getUsername(), ready));
        }
    }

    private void redirect() {
        prepare();

        StartEndpoint.removePendingGame(gameId);
        GameEndpoint.addRedirectingGame(gameId, this);

        sendJsonMessage(new RedirectMessage(gameId));
    }

    public boolean checkActivePlayers() {
        return eruditGame.checkActivePlayers();
    }

    public synchronized boolean setActiveAndCheck(Session session, Player player) {
        setPlayerStatus(player, PlayerStatus.ACTIVE);
        addSession(session, player);
        return checkActivePlayers();
    }

//    public boolean checkTurnAndReset(Session session) {
//        synchronized(lock) {
//            Player player = sessions.get(session);
//            Player nextMove = getNextMove();
//            if (player == nextMove) {
//                nextMove();
//                timer.start();
//            }
//            return player == nextMove;
//        }
//    }

    public boolean checkTurn(Session session) {
        Player player = sessions.get(session);
        return player == getNextMove();
    }

    public boolean skipTurn(Player player) {
        return eruditGame.skipTurn(player);
    }

    private void resetSkippedTurns() {
        eruditGame.resetSkippedTurns();
    }

    public List<Letter> changeLetters(Player player, List<Letter> letters) {
        return eruditGame.changeLetters(player, letters);
    }

    public Map<String, Integer> computeMove(List<Move> moves, Player player) throws GameException {
        try {
            return eruditGame.computeMove(moves, player);
//                resetSkippedTurns();
//                timer.start();
        } catch (GameException e) {
            eruditGame.cancelMoves();
            throw e;
        }
    }

    public void setPlayerStatus(Player player, PlayerStatus playerStatus) {
        player.setPlayerStatus(playerStatus);
    }

    private void setRedirectingPlayers() {
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

    private void prepare() {
        eruditGame = new EruditGame();
        eruditGame.setPlayers(sessions.values());

        setRedirectingPlayers();
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

    public static class Opponent {

        public Opponent(User user, boolean ready) {
            this.ready = ready;
            this.user = user;
        }

        User user;
        boolean ready;

        public boolean isReady() {
            return ready;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
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