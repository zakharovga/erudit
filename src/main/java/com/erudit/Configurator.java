package com.erudit;

import com.erudit.data.WordDB;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
                "authenticationFilter", new AuthenticationFilter()
        );
        registration.setAsyncSupported(true);
        registration.addMappingForUrlPatterns(null, false, "/login", "/start", "/game", "/register");

        WordDB.cacheDictionary();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) { }
}