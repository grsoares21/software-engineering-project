package api;

import db.DatabaseHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

public class AuthenticateUser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionToken = req.getAttribute("session_token").toString();

        Connection conn = DatabaseHelper.getDatabaseConnection();
        String querySQL = "SELECT * FROM users WHERE session_token = '" + sessionToken + "'";

        Statement stmt = null;
        try {
            JSONObject userCredentialsJSON = new JSONObject();

            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(querySQL);

            if(result.next()) {
                userCredentialsJSON.put("userAuthenticated", "Y");
                userCredentialsJSON.put("type", result.getInt("type"));
                userCredentialsJSON.put("name", result.getString("name"));
            } else {
                userCredentialsJSON.put("userAuthenticated", "N");
            }
            resp.setContentType("application/json");
            resp.getOutputStream().print(userCredentialsJSON.toString());

            stmt.close();
            conn.close();
            result.close();
        } catch (SQLException e) {
            System.err.println("An error occurred while trying to search for product in the database.");
            e.printStackTrace();
        }

    }
}
