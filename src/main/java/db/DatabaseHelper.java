package db;

import java.sql.*;

public class DatabaseHelper {
    public static void initializeDatabase() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = getDatabaseConnection();

            DatabaseMetaData metaData = conn.getMetaData();
            //if the standard tables of the database weren't yet created, creates them
            ResultSet tables = metaData.getTables(null, null, "offered_products", null);
            if(!tables.next()) {
                Statement stmt = conn.createStatement();
                String sql = "CREATE TABLE offered_products (" +
                        "    id integer NOT NULL CONSTRAINT offered_products_pk PRIMARY KEY AUTOINCREMENT," +
                        "    name varchar(50) NOT NULL," +
                        "    product_description text NOT NULL," +
                        "    defect_description text NOT NULL," +
                        "    category tinyint NOT NULL," +
                        "    final_date date NOT NULL" +
                        ")";
                stmt.executeUpdate(sql);
                stmt.close();
            }

            tables = metaData.getTables(null, null, "product_photos", null);
            if(!tables.next()) {
                Statement stmt = conn.createStatement();
                String sql = "CREATE TABLE product_photos (" +
                        "    id integer NOT NULL CONSTRAINT product_photos_pk PRIMARY KEY AUTOINCREMENT," +
                        "    url varchar(300) NOT NULL," +
                        "    offered_product_id integer NOT NULL," +
                        "    CONSTRAINT product_photos_offered_product FOREIGN KEY (offered_product_id)" +
                        "    REFERENCES offered_products (id)" +
                        ")";
                stmt.executeUpdate(sql);
                stmt.close();
            }

            conn.close();
        } catch (Exception e) {
            System.err.println("An error occurred while initializing the database.");
            e.printStackTrace();
        }
    }

    public static Connection getDatabaseConnection() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:eng_software.db");
        } catch (Exception e) {
            System.err.println("An error occurred while trying to connect to the database.");
            e.printStackTrace();
        }

        return conn;
    }

}
