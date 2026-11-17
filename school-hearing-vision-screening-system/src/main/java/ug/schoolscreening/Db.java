package ug.schoolscreening;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
    private Db() {}

    private static String setting(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    public static Connection getConnection() throws SQLException {
        String url = setting("SCHOOL_DB_URL",
                "jdbc:mysql://localhost:3306/school_screening?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String user = setting("SCHOOL_DB_USER", "root");
        String password = setting("SCHOOL_DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }
}
