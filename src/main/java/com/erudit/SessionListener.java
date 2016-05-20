package com.erudit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zakhar on 29.04.2016.
 */

@WebListener
public class SessionListener implements HttpSessionListener {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        LOGGER.debug("Session " + event.getSession().getId() + " created.");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {

        HttpSession httpSession = event.getSession();
        Object list = httpSession.getAttribute("WS_SESSION");
        if(list != null) {
            Session wsSession = ((List<Session>)list).get(0);
            try {
                wsSession.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
                        "Соединение разорвано, т.к. Вы вышли из своего профиля"));
            }
            catch (IOException ignore) { }
        }
    }
}