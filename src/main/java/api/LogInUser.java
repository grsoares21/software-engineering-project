package api;

import db.DatabaseHelper;
import org.apache.commons.codec.digest.DigestUtils;
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

public class LogInUser extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getAttribute("username").toString();
        String password = req.getAttribute("password").toString();

        String hashedPassword = DigestUtils.sha256Hex(password);

        Connection conn = DatabaseHelper.getDatabaseConnection();
        String querySQL = "SELECT * FROM users WHERE username = '" + username + "'";

        Statement stmt = null;
        try {
            JSONObject loginResultJSON = new JSONObject();

            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(querySQL);

            if(result.next()) {
                String actualHashedPassword = result.getString("password");
                if(actualHashedPassword.equals(hashedPassword)) {
                    loginResultJSON.put("loginResult", "success");
                    req.getSession().setAttribute("session_token", result.getString("session_token"));
                } else {
                    loginResultJSON.put("loginResult", "incorrect_password");
                }
            } else {
                loginResultJSON.put("loginResult", "inexistent_user");
            }
            resp.setContentType("application/json");
            resp.getOutputStream().print(loginResultJSON.toString());

            stmt.close();
            conn.close();
            result.close();
        } catch (SQLException e) {
            System.err.println("An error occurred while trying to search for product in the database.");
            e.printStackTrace();
        }

    }
}
