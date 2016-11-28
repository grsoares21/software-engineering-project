package api;

import db.DatabaseHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.sql.Result;
import java.io.IOException;
import java.sql.*;

public class RegisterProducts extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String category = req.getParameter("category"); 
        String productDescription = req.getParameter("productDescription");
        String defectDescription = req.getParameter("defectDescription");
        String finalDate = req.getParameter("finalDate");
        String photoUrls[] = req.getParameterValues("photoUrls[]");

        String sessionToken = req.getSession().getAttribute("session_token").toString();
        Connection conn = DatabaseHelper.getDatabaseConnection();

        Statement stmt = null;
        try {
            stmt = conn.createStatement();

            String getUserSQL = "SELECT id FROM users WHERE session_token='" + sessionToken + "'";
            ResultSet userResult = stmt.executeQuery(getUserSQL);
            int userId = userResult.getInt("id");

            String insertProductSQL = "INSERT INTO offered_products (name, description, defect_description, category, final_date, offering_user_id)"
                    + " VALUES (" + "'" + name + "'" + ", " +
                    "'" + productDescription + "'"  +  ", " +
                    "'" + defectDescription + "'" + ", " +
                    category + ", " +
                    "'" + finalDate + "'" + ", " +
                    userId + ")";
            stmt.executeUpdate(insertProductSQL);
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            generatedKeys.next();
            int productId = generatedKeys.getInt(1);

            for(String photoUrl : photoUrls) {
                String insertPhotoSQL = "INSERT INTO product_photos (url, offered_product_id) VALUES (" +
                        "'" + photoUrl + "', " + productId + ")";
                stmt.executeUpdate(insertPhotoSQL);
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("An error occurred while trying to insert products in the database.");
            e.printStackTrace();
        }
    }
}
    

