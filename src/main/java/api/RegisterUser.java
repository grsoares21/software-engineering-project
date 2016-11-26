package api;

import db.DatabaseHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class RegisterUser extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String password = req.getParameter("password");
        String username = req.getParameter("username");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        /*
          0 - buyer
          1 - seller
          2 - manager
        */
        int type = Integer.parseInt(req.getParameter("type"));
        String cnpj = req.getParameter("cnpj");
        String hashedPassword = DigestUtils.sha256Hex(password);

        Random rand = new Random();
        String sessionToken = RandomStringUtils.randomAlphanumeric(64);

        Connection conn = DatabaseHelper.getDatabaseConnection();

        String querySQL;

        if(type == 1) {
            querySQL = "INSERT INTO users (password, username, name, email, type, cnpj, session_token)"
                    + " VALUES (" + "'" + hashedPassword + "'"
                    + ", " + "'" + username + "'"
                    + ", " + "'" + name + "'"
                    + ", " + "'" + email + "'"
                    + ", " + type
                    + ", " + "'" + cnpj + "'"
                    + ", " + "'" + sessionToken + "'" + ")";
        } else {
            querySQL = "INSERT INTO users (password, username, name, email, type, session_token)"
                    + " VALUES (" + "'" + hashedPassword + "'"
                    + ", " + "'" + username + "'"
                    + ", " + "'" + name + "'"
                    + ", " + "'" + email + "'"
                    + ", " + type
                    + ", " + "'" + sessionToken + "'" + ")";
        }

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(querySQL);

            req.getSession().setAttribute("session_token", sessionToken);
    
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("An error occurred while trying to insert user in the database.");
            e.printStackTrace();
        }
    }
}
