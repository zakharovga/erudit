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
                = "INSERT INTO users (email, username, rating, password) "
                + "VALUES (?, ?, ?, ?)";
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getUsername());
            ps.setDouble(3, user.getRating());
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
                user.setRating(rs.getDouble("rating"));
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

    public static boolean updateInfo(String email, double newRating, int newGames) {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "UPDATE users SET rating = ?, games = ? WHERE email = ?";

        try {
            ps = connection.prepareStatement(query);
            ps.setDouble(1, newRating);
            ps.setInt(2, newGames);
            ps.setString(3, email);
            return ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
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
                user.setRating(rs.getDouble("rating"));
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
