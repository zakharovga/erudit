package com.erudit.data;

import com.erudit.PasswordUtil;
import com.erudit.User;

import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * Created by zakharov_ga on 08.02.2016.
 */
public class UserDB {

    public static void insert(User user, String password) throws SQLException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        PreparedStatement ps = null;

        String query
                = "INSERT INTO users (email, username, raiting, password) "
                + "VALUES (?, ?, ?, ?)";
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getUsername());
            ps.setInt(3, user.getRaiting());
            ps.setString(4, password);

            ps.executeUpdate();

        } finally {
            DBUtil.closePreparedStatement(ps);
            pool.freeConnection(connection);
        }
    }

    public static User select(String email, String password) {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        String hashedPassword;
        try {
            hashedPassword = PasswordUtil.hashPassword(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            rs = ps.executeQuery();
            User user = null;

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(email);
                user.setUsername(rs.getString("username"));
                user.setRaiting(rs.getInt("raiting"));
                user.setGames(rs.getInt("games"));
            }
            return user;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closePreparedStatement(ps);
            pool.freeConnection(connection);
        }
    }

    public static User select(String email) {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM users WHERE email = ?";

        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, email);
            rs = ps.executeQuery();
            User user = null;
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(email);
                user.setUsername(rs.getString("username"));
                user.setRaiting(rs.getInt("raiting"));
            }
            return user;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closePreparedStatement(ps);
            pool.freeConnection(connection);
        }
    }

//    public static boolean emailExists(String email) {
//        ConnectionPool pool = ConnectionPool.getInstance();
//        Connection connection = pool.getConnection();
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//
//        String query = "SELECT Email FROM User "
//                + "WHERE Email = ?";
//        try {
//            ps = connection.prepareStatement(query);
//            ps.setString(1, email);
//            rs = ps.executeQuery();
//            return rs.next();
//        } catch (SQLException e) {
//            System.err.println(e);
//            return false;
//        } finally {
//            DBUtil.closeResultSet(rs);
//            DBUtil.closePreparedStatement(ps);
//            pool.freeConnection(connection);
//        }
//    }
}
