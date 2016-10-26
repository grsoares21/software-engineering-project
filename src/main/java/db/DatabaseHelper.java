package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseHelper {
    public static void initializeDatabase() {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:eng_software.db");
        } catch (Exception e) {
            System.err.println("An error occured while trying to connect to the database during initialization");
            e.printStackTrace();
        }

    }

}
