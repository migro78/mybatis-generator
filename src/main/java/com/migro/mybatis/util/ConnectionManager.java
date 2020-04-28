package com.migro.mybatis.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by Owen on 8/21/16.
 */
public class ConnectionManager {
    protected static Logger logger = LogManager.getLogger();
    private static final String DB_URL = "jdbc:sqlite:./config/sqlite3.db";

    public static Connection getConnection() throws Exception {
        Class.forName("org.sqlite.JDBC");
        File file = new File(DB_URL.substring("jdbc:sqlite:".length())).getAbsoluteFile();
        logger.info("database FilePath :{}", file.getAbsolutePath());
        Connection conn = DriverManager.getConnection(DB_URL);
        return conn;
    }
}
