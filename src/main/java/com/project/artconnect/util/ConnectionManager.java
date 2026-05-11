package com.project.artconnect.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/artconnect?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";

    private static final String DB_USER = "root";

    private static final String DB_PASSWORD = "admin";

    /** Private constructor */
    private ConnectionManager() {
    }

    /**
     * Returns a JDBC connection.
     *
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                DB_URL,
                DB_USER,
                DB_PASSWORD);
    }
}