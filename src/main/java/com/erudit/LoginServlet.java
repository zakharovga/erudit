package com.erudit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by zakhar on 19.03.2016.
 */

@WebServlet(
        name = "loginServlet",
        urlPatterns = "/login",
        loadOnStartup = 1
)
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession httpSession = request.getSession();
        if(request.getParameter("logout") != null) {
            httpSession.invalidate();
            response.sendRedirect("login");
            return;
        }
        view("login", request, response);
    }

    private void view(String view, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/view/"+view+".jsp").forward(request, response);
    }
}