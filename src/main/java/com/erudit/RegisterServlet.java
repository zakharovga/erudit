package com.erudit;

import com.erudit.data.UserDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zakharov_ga on 10.02.2016.
 */
@WebServlet(
        name = "registerServlet",
        urlPatterns = "/register",
        loadOnStartup = 1
)
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        view("register", request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        List<RegistrationError> errors = new ArrayList<>();

        if(!Validator.validateEmail(email))
            errors.add(RegistrationError.EMAIL_VALIDATION_ERROR);

        if(!Validator.validateUsername(username))
            errors.add(RegistrationError.USERNAME_VALIDATION_ERROR);

        if(!Validator.validatePassword(password))
            errors.add(RegistrationError.PASSWORD_VALIDATION_ERROR);

        if(errors.size() != 0) {
            request.setAttribute("errors", errors);
            view("register", request, response);
        }
        else {
            User user = new User(email, username, User.DEFAULT_RATING, false);
            try {
                String hashedPassword = PasswordUtil.hashPassword(password);
                UserDB.insert(user, hashedPassword);
                HttpSession httpSession = request.getSession();
                httpSession.setAttribute("user", user);

                response.sendRedirect("start");
            } catch (SQLException e) {
                if (e.getSQLState().startsWith("23")) {
                    errors.add(RegistrationError.DUPLICATE_ERROR);
                    request.setAttribute("errors", errors);
                    view("register", request, response);
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    private void view(String view, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/view/"+view+".jsp").forward(request, response);
    }

//    private void list(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/start"));
//    }
}