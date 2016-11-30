package api;

import db.DatabaseHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DeleteAuction extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String productId = req.getParameter("productId");
        String sessionToken = req.getSession().getAttribute("session_token").toString();

        Connection conn = DatabaseHelper.getDatabaseConnection();

        String querySQL = "DELETE FROM offered_products WHERE";
        querySQL = querySQL + " id = " + productId;

        Statement stmt;
        try {
            stmt = conn.createStatement();
            String getUserSQL = "SELECT type FROM users WHERE session_token='" + sessionToken + "'";
            ResultSet userResult = stmt.executeQuery(getUserSQL);
            if(userResult.getInt("type") != 2) {
                return; //apenas admins podem deletar leilões
            }

            stmt.executeUpdate(querySQL);

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("An error occurred while trying to delete an auction from the database.");
            e.printStackTrace();
        }

    }
}
