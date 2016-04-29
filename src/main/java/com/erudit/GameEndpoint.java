package com.erudit;

import com.erudit.exceptions.*;
import com.erudit.messages.*;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zakharov_ga on 30.12.2015.
 */
@ServerEndpoint(value = "/game/{gameId}",
                encoders = MessageEncoder.class,
                decoders = MessageDecoder.class,
                configurator = StartEndpoint.GetHttpSessionConfigurator.class)
public class GameEndpoint {

    private static final Map<String, Game> usernames = new ConcurrentHashMap<>();
    private static final Map<Long, Game> games = new ConcurrentHashMap<>();
    private static final Map<Long, Game> activeGames = new ConcurrentHashMap<>();
    private static final Map<Long, Game> redirectingGames = new ConcurrentHashMap<>();
    private static Map<Session, Game> allSessions = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("gameId") long gameId) {

        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());

        openConnection(session, httpSession, gameId);
    }

    private void openConnection(Session session, HttpSession httpSession, long gameId) {
        String httpSessionId = httpSession.getId();
        Game game = GameEndpoint.getGame(gameId);
        if(game == null) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                        "Произошла ошибка"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Player player = game.getPlayerByHttpSessionId(httpSessionId);
            if(player == null) {
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                            "Произошла ошибка"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                game.setActiveAndCheck(session, httpSession, player);
            }
        }
    }

    @OnMessage
    public void onMessage(ClientMessage message, Session session, @PathParam("gameId") long gameId) {
        processMessage(session, message, gameId);
    }

    @OnClose
    public void onClose(Session session) {

        closeSession(session);
    }

    private void closeSession(Session session) {

        Game game = GameEndpoint.getGame(session);
        if (game == null)
            return;
        game.disconnectPlayer(session);
    }

    private void processMessage(Session session, ClientMessage clientMessage, long gameId) {
        Game game = GameEndpoint.getGame(gameId);
        if(game == null) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                        "Произошла ошибка"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            if("PLAYER_MADE_MOVE".equalsIgnoreCase(clientMessage.getAction())) {
                List<Move> moves = clientMessage.getMove();

                game.processMoves(session, moves);
            }
            else if("PLAYER_CHANGED_LETTERS".equalsIgnoreCase(clientMessage.getAction())) {
                List<Letter> changedLetters = clientMessage.getLetters();

                game.processChangingLetters(session, changedLetters);
            }
        }
    }

    public static void addRedirectingGame(long gameId, Game game) {
        redirectingGames.put(gameId, game);
    }

    public static void addActiveGame(long gameId, Game game) {
        activeGames.put(gameId, game);
    }

    public static void addGame(long gameId, Game game) {
        games.put(gameId, game);
    }

    public static void addSession(Session session, Game game) {
        allSessions.put(session, game);
    }

    public static Game getGame(String username) {
        return usernames.get(username);
    }

    public static Game getGame(Session session) {
        return allSessions.get(session);
    }

    public static Game getGame(long gameId) {
        return games.get(gameId);
    }

    public static void addUsername(String username, Game game) {
        usernames.put(username, game);
    }

    public static void removeUsername(String username) {
        usernames.remove(username);
    }

    public static void removeGame(long gameId) {
        games.remove(gameId);
    }

    public static void removeActiveGame(long gameId) {
        GameEndpoint.activeGames.remove(gameId);
    }

    public static void removeRedirectingGame(long gameId) {
        GameEndpoint.redirectingGames.remove(gameId);
    }

    public static void removeSession(Session session) {
        allSessions.remove(session);
    }
}