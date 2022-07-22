package com.mchern1kov.config;

import com.google.inject.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Singleton
public class DbConnectionManager {

    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "";
    private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    public Connection getDbConnection() throws SQLException {
        return DriverManager.getConnection(DB_CONNECTION, DB_USERNAME, DB_PASSWORD);
    }
}
