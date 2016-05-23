package com.erudit;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zakharov_ga on 09.02.2016.
 */
public class AuthenticationFilter implements Filter {

    private static final AtomicLong guestIdSequence = new AtomicLong(1L);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpSession httpSession = ((HttpServletRequest)request).getSession();
        if(httpSession.getAttribute("user") == null) {
            User user = new User();

            String username = "Гость" + getGuestId();

            user.setUsername(username);
            user.setRaiting(1200);
            user.setGuest(true);
            user.setGames(0);

            httpSession.setAttribute("user", user);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig config) throws ServletException { }

    @Override
    public void destroy() { }

    private static long getGuestId() {
        return guestIdSequence.getAndIncrement();
    }
}