package com.erudit;

import com.erudit.messages.*;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zakhar on 29.02.2016.
 */

@ServerEndpoint(value = "/start",
                encoders = MessageEncoder.class,
                decoders = MessageDecoder.class,
                configurator = StartEndpoint.GetHttpSessionConfigurator.class)
public class StartEndpoint {

    // игры, ожидающие начала
    public static final Map<Long, Game> pendingGames = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {

        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());

        openConnection(session, httpSession);
    }

    private void openConnection(Session session, HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");

        List<String> actionList = session.getRequestParameterMap().get("action");

        if (actionList != null && actionList.size() == 1) {
            String action = actionList.get(0);

            if ("CREATE".equalsIgnoreCase(action)) {
                createGame(session, httpSession, user);

            } else if ("JOIN".equalsIgnoreCase(action)) {
                joinGame(session, httpSession, user);
            }
        }
    }

    private void createGame(Session session, HttpSession httpSession, User user) {

        String username = user.getUsername();
        Player creator = new Player(user);

        Game game = Game.queueGame();
        long gameId = game.getGameId();
        game.addSession(session, creator);
        game.addHttpSession(httpSession.getId(), creator);
        game.setCreator(user.getUsername());

        Game oldGame = GameEndpoint.getGame(username);
        if (oldGame != null) {
            Session oldSession = oldGame.getSession(username);
            if (oldSession != null)
                game.closeSession(oldSession, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                        "Соединение разорвано, т.к. Вы начали новую игру"));
        }
        GameEndpoint.addGame(gameId, game);
        GameEndpoint.addSession(session, game);
        GameEndpoint.addUsername(username, game);
        httpSession.setAttribute("WS_SESSION", Collections.singletonList(session));
        SessionRegistry.addSession(session, httpSession);
    }

    private void joinGame(Session session, HttpSession httpSession, User user) {
        List<String> gameIdList = session.getRequestParameterMap().get("gameid");

        long gameId = Long.parseLong(gameIdList.get(0));

        Game game = GameEndpoint.getGame(gameId);
        if (game == null) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                        "Эта игра больше не существует"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            game.joinPlayer(session, httpSession, user);
        }
    }

    @OnClose
    public void onClose(Session session) {
        closeSession(session);
    }

    @OnMessage
    public void onMessage(ClientMessage message, Session session) {
        processMessage(session, message);
    }

    private void processMessage(Session session, ClientMessage clientMessage) {
        if ("OPPONENT_READY".equalsIgnoreCase(clientMessage.getAction())) {
            Game game = GameEndpoint.getGame(session);
            if (game == null)
                return;

            String username = clientMessage.getReadyOpponent();

            Player player = game.getPlayer(username);
            if (player == null)
                return;

            boolean ready = clientMessage.isReady();
            game.setReadyPlayerAndCheck(session, player, ready);
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

        game.disconnectPlayer(session);
    }

    public static class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

        @Override
        public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {

            HttpSession httpSession = (HttpSession)request.getHttpSession();
            config.getUserProperties().put(HttpSession.class.getName(), httpSession);
        }
    }

    public static Map<Long, Game> getPendingGames() {
        return pendingGames;
    }

    public static void addPendingGame(long gameId, Game game) {
        pendingGames.put(gameId, game);
    }

    public static void removePendingGame(long gameId) {
        pendingGames.remove(gameId);
    }
}