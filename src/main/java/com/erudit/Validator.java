package com.erudit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zakharov_ga on 10.02.2016.
 */
public class Validator {

    private static final String EMAIL_PATTERN = "^.+@.+(\\.[^\\.]+)+$";
    private static final String USERNAME_PATTERN = "[a-zA-Z_0-9\\p{IsCyrillic}\\s]{3,45}";
    private static final String PASSWORD_PATTERN = "[a-zA-Z_0-9]{5,15}";

    public static boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validateUsername(String username) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public static boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}