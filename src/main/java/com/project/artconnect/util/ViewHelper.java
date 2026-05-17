package com.project.artconnect.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.project.artconnect.config.DatabaseConfig;

/**
 * Utility to query SQL views and return results as a list of column→value maps.
 * Usage: ViewHelper.query("SELECT * FROM V_Artwork_By_Artist")
 */
public class ViewHelper {

    public static List<Map<String, String>> query(String sql, Object... params) {
        List<Map<String, String>> rows = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            while (rs.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 1; i <= cols; i++) {
                    String val = rs.getString(i);
                    row.put(meta.getColumnLabel(i), val == null ? "" : val);
                }
                rows.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    /** Shorthand for a single-column, single-row value */
    public static String scalar(String sql, Object... params) {
        List<Map<String, String>> rows = query(sql, params);
        if (rows.isEmpty()) return "";
        return rows.get(0).values().iterator().next();
    }
}