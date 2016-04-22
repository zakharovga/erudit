package com.erudit.data;

import com.erudit.EruditGame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zakharov_ga on 21.04.2016.
 */
public class WordDB {

    public static boolean cacheDictionary() {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection.setAutoCommit(false);
            String query = "SELECT * FROM words";
            ps = connection.prepareStatement(query);
            ps.setFetchSize(1000);
            rs = ps.executeQuery();
            while(rs.next()) {
                String word = rs.getString("word");
                EruditGame.addWord(word);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closePreparedStatement(ps);
            pool.freeConnection(connection);
        }
    }
}