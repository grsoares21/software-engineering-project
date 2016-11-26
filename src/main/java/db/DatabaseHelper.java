package db;

import java.sql.*;

public class DatabaseHelper {
    public static void initializeDatabase() {
        Connection conn;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = getDatabaseConnection();

            DatabaseMetaData metaData = conn.getMetaData();
            //if the standard tables of the database weren't yet created, creates them
            ResultSet tables;
            tables = metaData.getTables(null, null, "bids", null);
            if(!tables.next()) {
                createBidsTable(conn);
            }

            tables = metaData.getTables(null, null, "complaints", null);
            if(!tables.next()) {
                createComplaintsTable(conn);
            }

            tables = metaData.getTables(null, null, "offered_products", null);
            if(!tables.next()) {
                createOfferedProductsTable(conn);
            }

            tables = metaData.getTables(null, null, "product_photos", null);
            if(!tables.next()) {
                createProductPhotosTable(conn);
            }

            tables = metaData.getTables(null, null, "sales_reports", null);
            if(!tables.next()) {
                createSalesReportsTable(conn);
            }

            tables = metaData.getTables(null, null, "users", null);
            if(!tables.next()) {
                createUsersTable(conn);
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

    private static void createBidsTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE bids (" +
                " id integer NOT NULL CONSTRAINT bids_pk PRIMARY KEY AUTOINCREMENT," +
                " bid_value decimal(10,2) NOT NULL," +
                " offered_product_id integer NOT NULL," +
                " bidder_id integer NOT NULL," +
                " CONSTRAINT bids_offered_products FOREIGN KEY (offered_product_id)" +
                " REFERENCES offered_products (id)," +
                " CONSTRAINT bids_users FOREIGN KEY (bidder_id)" +
                " REFERENCES users (id)" +
                ")";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    private static void createComplaintsTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE complaints (" +
                " id integer NOT NULL CONSTRAINT complaints_pk PRIMARY KEY AUTOINCREMENT," +
                " reporting_user integer NOT NULL," +
                " reported_user integer NOT NULL," +
                " justification text NOT NULL," +
                " CONSTRAINT complaints_reporting_user FOREIGN KEY (reporting_user)" +
                " REFERENCES users (id)," +
                " CONSTRAINT complaints_reported_user FOREIGN KEY (reported_user)" +
                " REFERENCES users (id)" +
                ")";
        stmt.executeUpdate(sql);
        stmt.close();
    }
    private static void createOfferedProductsTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE offered_products (" +
                " id integer NOT NULL CONSTRAINT offered_products_pk PRIMARY KEY AUTOINCREMENT," +
                " name varchar(50) NOT NULL," +
                " description text NOT NULL," +
                " defect_description text NOT NULL," +
                " category tinyint NOT NULL," +
                " final_date date NOT NULL," +
                " offering_user_id integer NOT NULL," +
                " CONSTRAINT offering_user FOREIGN KEY (offering_user_id)" +
                " REFERENCES users (id)" +
                ")";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    private static void createProductPhotosTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE product_photos (" +
                " id integer NOT NULL CONSTRAINT product_photos_pk PRIMARY KEY AUTOINCREMENT," +
                " url varchar(300) NOT NULL," +
                " offered_product_id integer NOT NULL," +
                " CONSTRAINT product_photos_offered_product FOREIGN KEY (offered_product_id)" +
                " REFERENCES offered_products (id)" +
                ")";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    private static void createSalesReportsTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE sales_reports (" +
                " id integer NOT NULL CONSTRAINT id PRIMARY KEY AUTOINCREMENT," +
                " seller_user integer NOT NULL," +
                " buyer_id integer NOT NULL," +
                " CONSTRAINT sales_reports_seller_users FOREIGN KEY (seller_user)" +
                " REFERENCES users (id)," +
                " CONSTRAINT sales_reports_buyer_user FOREIGN KEY (buyer_id)" +
                " REFERENCES users (id)" +
                ")";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    private static void createUsersTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE users (" +
                " id integer NOT NULL CONSTRAINT users_pk PRIMARY KEY AUTOINCREMENT," +
                " password character(64) NOT NULL," +
                " username varchar(50) NOT NULL CONSTRAINT username_unique UNIQUE," +
                " name varchar(100) NOT NULL," +
                " email varchar(100) NOT NULL," +
                " type tinyint NOT NULL," +
                " cnpj character(14)," +
                " session_token character(64) CONSTRAINT session_token_unique UNIQUE" +
                ")";
        stmt.executeUpdate(sql);
        stmt.close();
    }

}
