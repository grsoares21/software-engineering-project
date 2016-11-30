package api;

import db.DatabaseHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class RegisterBid extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String bidValue = req.getParameter("bidValue");
        String productId = req.getParameter("productId");
        String sessionToken = req.getSession().getAttribute("session_token").toString();

        Connection conn = DatabaseHelper.getDatabaseConnection();

        try {
            Statement stmt = conn.createStatement();
            String getUserSQL = "SELECT id FROM users WHERE session_token='" + sessionToken + "'";
            ResultSet userResult = stmt.executeQuery(getUserSQL);
            int userId = userResult.getInt("id");

            String insertBidSQL = "INSERT INTO bids (bid_value, offered_product_id, bidder_id)"
                    + " VALUES (" + "'" + bidValue + "'"
                    + ", " + productId
                    + ", " + userId + ")";


            stmt.executeUpdate(insertBidSQL);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("An error occurred while trying to insert a bid in the database.");
            e.printStackTrace();
        }
    }
}
