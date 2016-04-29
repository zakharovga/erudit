package com.erudit;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zakhar on 29.04.2016.
 */
public class SessionRegistry {

    private static final Map<Session, HttpSession> sessions = new ConcurrentHashMap<>();

    public static HttpSession getHttpSession(Session session) {
        return sessions.get(session);
    }

    public static void addSession(Session session, HttpSession httpSession) {
        sessions.put(session, httpSession);
    }
}