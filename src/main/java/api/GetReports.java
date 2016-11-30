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

public class GetReports extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionToken = req.getSession().getAttribute("session_token").toString();
        
        Connection conn = DatabaseHelper.getDatabaseConnection();

        Statement stmt;
        try {
            stmt = conn.createStatement();

            String getUserSQL = "SELECT id FROM users WHERE session_token='" + sessionToken + "'";
            ResultSet userResult = stmt.executeQuery(getUserSQL);
            int userId = userResult.getInt("id");

            String querySQL = "SELECT * FROM reports WHERE";
            querySQL = querySQL + " subject_user = " + userId;
            // consulta banco de relatorios
            ResultSet reportsResult = stmt.executeQuery(querySQL);

            JSONArray responseArray = new JSONArray();

            while(reportsResult.next()) {
                // informacoes do relatorio
                JSONObject reportObject = new JSONObject();
                reportObject.put("id", reportsResult.getInt("id"));

                int objectUserId = reportsResult.getInt("object_user");
                if(!reportsResult.wasNull()) {
                    reportObject.put("objectUserInfo", getUserInfo(objectUserId, conn));
                }

                reportObject.put("date", reportsResult.getString("date"));
                reportObject.put("type", reportsResult.getInt("type"));
                reportObject.put("text", reportsResult.getString("text"));

                responseArray.put(reportObject);
            }
            resp.setContentType("application/json");
            resp.getOutputStream().print(responseArray.toString());
            
            stmt.close();
            conn.close();
            reportsResult.close();
        } catch (SQLException e) {
            System.err.println("An error occurred while trying to get reports information from the database.");
            e.printStackTrace();
        }

    }

    private JSONObject getUserInfo(int userId, Connection dbConnection) throws SQLException {
        String querySQL = "SELECT * FROM users WHERE id = '" + userId + "'";
        JSONObject userObject = new JSONObject();
        Statement stmt = dbConnection.createStatement();

        ResultSet userResult = stmt.executeQuery(querySQL);
        if(userResult.next()) {
            userObject.put("name", userResult.getString("name"));
            userObject.put("email", userResult.getString("email"));
        }

        userResult.close();
        return userObject;
    }
}
