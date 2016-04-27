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

        boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if(ajax) {
            String email = request.getParameter("email").trim();
            String username = request.getParameter("username").trim();
            String password = request.getParameter("password").trim();

            RegistrationMessage message = new RegistrationMessage();
            List<RegistrationError> errors = new ArrayList<>();

            if (!Validator.validateEmail(email))
                errors.add(RegistrationError.EMAIL_VALIDATION_ERROR);

            if (!Validator.validateUsername(username))
                errors.add(RegistrationError.USERNAME_VALIDATION_ERROR);

            if (!Validator.validatePassword(password))
                errors.add(RegistrationError.PASSWORD_VALIDATION_ERROR);

            if (errors.size() != 0) {
                message.setValid(false);
                message.setErrors(errors);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(MessageEncoder.MAPPER.writeValueAsString(message));
            } else {
                User user = new User(email, username, User.DEFAULT_RATING, false);
                try {
                    String hashedPassword = PasswordUtil.hashPassword(password);
                    UserDB.insert(user, hashedPassword);
                    HttpSession httpSession = request.getSession();
                    httpSession.setAttribute("user", user);

                    message.setValid(true);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(MessageEncoder.MAPPER.writeValueAsString(message));
                } catch (SQLException e) {
                    if (e.getSQLState().startsWith("23")) {
                        errors.add(RegistrationError.DUPLICATE_ERROR);

                        message.setValid(false);
                        message.setErrors(errors);

                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(MessageEncoder.MAPPER.writeValueAsString(message));
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void view(String view, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/view/"+view+".jsp").forward(request, response);
    }

//    private void list(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/start"));
//    }

    private static class RegistrationMessage {
        private boolean valid;
        private List<RegistrationError> errors;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public List<RegistrationError> getErrors() {
            return errors;
        }

        public void setErrors(List<RegistrationError> errors) {
            this.errors = errors;
        }
    }
}