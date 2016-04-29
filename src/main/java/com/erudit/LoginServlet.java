package com.erudit;

import com.erudit.data.UserDB;

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = UserDB.select(email, password);
        if(user != null) {
            HttpSession oldHttpSession = request.getSession();
            oldHttpSession.invalidate();

            HttpSession newHttpSession = request.getSession();
            newHttpSession.setAttribute("user", user);
            redirect("/start", request, response);
        }
    }

    private void view(String view, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/view/"+view+".jsp").forward(request, response);
    }

    private void redirect(String path, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + path));
    }
}