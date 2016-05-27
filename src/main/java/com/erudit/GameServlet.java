package com.erudit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zakharov_ga on 27.05.2016.
 */
@WebServlet(
        name = "gameServlet",
        urlPatterns = "/game",
        loadOnStartup = 1
)
public class GameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setAttribute("pendingGames", StartEndpoint.getPendingGames());
        view("start", request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String stringGameId = request.getParameter("gameId");
        if(stringGameId == null) {
            list(request, response);
        }
        else {
            String httpSessionId = request.getSession().getId();

            long gameId = Long.parseLong(stringGameId);
            Game game = GameEndpoint.getGame(gameId);

            if(game == null) {
                list(request, response);
                return;
            }

            if(game.checkRedirectingPlayer(httpSessionId)){
                request.setAttribute("gameId", stringGameId);
                view("game", request, response);
//                list(request, response);
                return;
            }
            else
                list(request, response);
        }
    }

    private void view(String view, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/view/"+view+".jsp").forward(request, response);
    }

    private void list(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/start"));
    }
}