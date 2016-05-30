package com.erudit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zakharov_ga on 30.05.2016.
 */
@WebServlet(
        name = "monitorServlet",
        urlPatterns = "/monitor",
        loadOnStartup = 1
)
public class MonitorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setAttribute("pendingGames", StartEndpoint.getPendingGames());
        request.setAttribute("activeGames", GameEndpoint.getActiveGames());
        request.setAttribute("redirectingGames", GameEndpoint.getRedirectingGames());
        request.setAttribute("allSessions", GameEndpoint.getAllSessions());
        request.setAttribute("sessions", SessionRegistry.getSessions());

        view("monitor", request, response);
    }

    private void view(String view, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/view/"+view+".jsp").forward(request, response);
    }
}