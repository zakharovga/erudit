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
                encoders = MessageCodec.class,
                decoders = MessageCodec.class,
                configurator = StartEndpoint.GetHttpSessionConfigurator.class)
public class GameEndpoint {

    private static final Map<String, Game> usernames = new ConcurrentHashMap<>();
    private static final Map<Long, Game> games = new ConcurrentHashMap<>();
    private static final Map<Long, Game> activeGames = new ConcurrentHashMap<>();
    private static final Map<Long, Game> redirectingGames = new ConcurrentHashMap<>();
    private static Map<Session, Game> allSessions = new ConcurrentHashMap<>();

    public static final Object LOCK = new Object();


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
                boolean allActive = game.setActiveAndCheck(session, player);
                if(allActive) {
                    game.start();
                    for (Map.Entry<Session, Player> entry : game.getSessions().entrySet()) {
                        Session eachSession = entry.getKey();
                        Player eachPlayer = entry.getValue();
                        List<User> opponents = game.getOpponents(eachSession);

                        Message eachMessage = new GameStartedMessage(game, eachPlayer, opponents);
                        game.sendJsonMessage(eachSession, eachMessage);
                    }
                }
            }
        }
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("gameId") long gameId) {
        try {
            ClientMessage clientMessage = Game.mapper.readValue(message, ClientMessage.class);
            processMessage(session, clientMessage, gameId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("closed!");
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
            if ("playerMadeMove".equalsIgnoreCase(clientMessage.getAction())) {
                Player player = game.getPlayer(session);
                if (player == null)
                    return;
                if (!game.checkTurn(session)) {
                    return;
                }
                List<Move> moves = clientMessage.getMove();
                if (moves == null || moves.size() == 0) {
                    if (game.skipTurn(player)) {
                        game.gameOver();
                    }

                    List<Letter> changedLetters = clientMessage.getLetters();
                    if (changedLetters != null && changedLetters.size() != 0) {

                        game.changeTurn();

                        List<Letter> newLetters = game.changeLetters(player, changedLetters);

                        Message playerMessage = new PlayerChangedLettersMessage(game, newLetters);
                        Message opponentMessage = new OpponentChangedLettersMessage(game, player.getUser().getUsername());

                        game.sendJsonMessage(session, playerMessage);
                        game.sendJsonMessageToOpponents(session, opponentMessage);
                    }
                } else {
                    try {
                        Map<String, Integer> words = game.computeMove(moves, player);

                        game.changeTurn();

                        Message playerMessage = new PlayerMadeMoveMessage(game, player, moves, words);
                        Message opponentMessage = new OpponentMadeMoveMessage(game, player, moves, words);

                        game.sendJsonMessage(session, playerMessage);
                        game.sendJsonMessageToOpponents(session, opponentMessage);
                    } catch (GameException e) {
                        Message message = ExceptionMessageFactory.getMessage(e);
                        game.sendJsonMessage(session, message);
                    }
                }
            }
        }
    }

    public static void addRedirectingGame(long gameId, Game game) {
        redirectingGames.put(gameId, game);
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
}