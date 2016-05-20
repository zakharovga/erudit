package com.erudit;

import com.erudit.data.WordDB;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import java.util.EnumSet;

/**
 * Created by zakharov_ga on 09.02.2016.
 */
@WebListener
public class Configurator implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event)
    {
        ServletContext context = event.getServletContext();

        FilterRegistration.Dynamic registration = context.addFilter(
                "loggingFilter", new LoggingFilter()
        );
        registration.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE,
                        DispatcherType.FORWARD, DispatcherType.ERROR),
                false, "/*"
        );

        registration = context.addFilter(
                "authenticationFilter", new AuthenticationFilter()
        );
        registration.setAsyncSupported(true);
        registration.addMappingForUrlPatterns(null, false, "/login", "/start", "/game", "/register");

        WordDB.cacheDictionary();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) { }
}