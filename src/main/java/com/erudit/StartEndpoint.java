package com.erudit;

import com.erudit.messages.*;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zakhar on 29.02.2016.
 */

@ServerEndpoint(value = "/start",
                encoders = MessageCodec.class,
                decoders = MessageCodec.class,
                configurator = StartEndpoint.GetHttpSessionConfigurator.class)
public class StartEndpoint {

    // игры, ожидающие начала
    public static final Map<Long, Game> pendingGames = new ConcurrentHashMap<>();

    public static Map<Long, Game> getPendingGames() {
        return pendingGames;
    }

    public static void addPendingGame(long gameId, Game game) {
        pendingGames.put(gameId, game);
    }

    public static void removePendingGame(long gameId) {
        pendingGames.remove(gameId);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {

        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());

        openConnection(session, httpSession);
    }

    private void openConnection(Session session, HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        String sessionId = httpSession.getId();

        List<String> actionList = session.getRequestParameterMap().get("action");

        if (actionList != null && actionList.size() == 1) {
            String action = actionList.get(0);

            if ("create".equalsIgnoreCase(action)) {
                createGame(session, sessionId, user);

            } else if ("join".equalsIgnoreCase(action)) {
                joinGame(session, sessionId, user);
            }
        }
    }

    private void createGame(Session session, String httpSessionId, User user) {

        String username = user.getUsername();
        Player creator = new Player(user);

        Game game = Game.queueGame();
        long gameId = game.getGameId();
        game.addSession(session, creator);
        game.addHttpSession(httpSessionId, creator);
        game.setCreator(user.getUsername());

        synchronized(GameEndpoint.LOCK) {
            Game oldGame = GameEndpoint.getGame(username);
            if (oldGame != null) {
                Session oldSession = oldGame.getSession(username);
                try {
                    oldSession.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                            "Соединение разорвано, т.к. Вы начали новую игру"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            GameEndpoint.addGame(gameId, game);
            GameEndpoint.addSession(session, game);
            GameEndpoint.addUsername(username, game);
        }
    }

    private void joinGame(Session session, String sessionId, User user) {
        List<String> gameIdList = session.getRequestParameterMap().get("gameid");

        String username = user.getUsername();
        long gameId = Long.parseLong(gameIdList.get(0));

        synchronized(GameEndpoint.LOCK) {
            Game game = GameEndpoint.getGame(gameId);
            if(game == null) {
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                            "Эта игра больше не существует"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(game.getGameStatus() == GameStatus.ACTIVE) {/////// gameStatus - volatile
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                            "Эта игра уже началась"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(game.size() > 3){
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                            "В этой игре уже максимум игроков"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(game.getSession(username) != null) {
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                            "Вы уже присоединились к этой игре"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                Game oldGame = GameEndpoint.getGame(username);
                if(oldGame != null) {
                    Session oldSession = oldGame.getSession(username);
                    try {
                        oldSession.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                                "Соединение разорвано, т.к. Вы начали новую игру"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Player joiner = new Player(user);
                game.addSession(session, joiner);
                game.addHttpSession(sessionId, joiner);

                GameEndpoint.addUsername(username, game);
                GameEndpoint.addSession(session, game);

                PlayerJoinedMessage playerJoinedMessage = new PlayerJoinedMessage(game, joiner);

                game.sendJsonMessageToOpponents(session, new OpponentJoinedMessage(user));
                game.sendJsonMessage(session, playerJoinedMessage);
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        closeSession(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            ClientMessage clientMessage = Game.mapper.readValue(message, ClientMessage.class);
            processMessage(session, clientMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(Session session, ClientMessage clientMessage) {
        if ("opponentReady".equalsIgnoreCase(clientMessage.getAction())) {
            Game game = GameEndpoint.getGame(session);
            if (game == null)
                return;

            String username = clientMessage.getReadyOpponent();
            if (username == null)
                return;

            Player player = game.getPlayer(username);
            if (player == null)
                return;

            boolean ready = clientMessage.isReady();
            synchronized (GameEndpoint.LOCK) {
                if(game.getGameStatus() != GameStatus.PENDING)
                    return;
                if (ready)
                    game.setPlayerStatus(player, PlayerStatus.READY);
                else {
                    game.setPlayerStatus(player, PlayerStatus.NOT_READY);
                }

                boolean playersReady = game.checkReadyPlayers();
                if (playersReady)
                    redirectGame(game);
                else {
                    game.sendJsonMessageToOpponents(session, new OpponentReadyMessage(username, ready));
                }
            }
        }
    }

//    @OnError
//    public void onError(Session session, Throwable e) {
//        System.out.println(session);
//        System.out.println("ERROR");
//        try {
//            session.close(new CloseReason(
//                    CloseReason.CloseCodes.UNEXPECTED_CONDITION, e.toString()
//            ));
//        }
//        catch(IOException ignore) {
//            System.out.println("ERROR");
//        }
//    }

    private void closeSession(Session session) {

        Game game = GameEndpoint.getGame(session);
        if (game == null)
            return;

        long gameId = game.getGameId();
        String username = game.getPlayer(session).getUsername();

        synchronized(GameEndpoint.LOCK) {
            GameEndpoint.removeUsername(username);
            game.removeSession(session);

            if (game.getGameStatus() != GameStatus.REDIRECTING) {
                if (game.size() == 0) {
                    GameEndpoint.removeGame(gameId);
                    removePendingGame(gameId);
                } else
                    System.out.println(game.getGameStatus());
                    game.sendJsonMessageToOpponents(session, new OpponentQuitMessage(username));
            }
        }
    }

    private void redirectGame(Game game) {

        long gameId = game.getGameId();
        game.prepare();

        StartEndpoint.removePendingGame(gameId);
        GameEndpoint.addRedirectingGame(gameId, game);

        game.sendJsonMessage(new RedirectMessage(gameId));
    }

    public static class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

        @Override
        public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {

            HttpSession httpSession = (HttpSession)request.getHttpSession();
            config.getUserProperties().put(HttpSession.class.getName(), httpSession);
        }
    }
}