package api;

import db.DatabaseHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

public class RegisterProducts extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String category = req.getParameter("category"); 
        String productDescription = req.getParameter("description");
        String defectDescription = req.getParameter("defect_description");
        String finalDate = req.getParameter("final_date");
        
        Connection conn = DatabaseHelper.getDatabaseConnection();
        
        String querySQL = "INSERT INTO offered_products (name, product_description, defect_description, category, final_date)"
                           + " VALUES (" + "'" + name + "'" + ", " + "'" + productDescription + "'"  +  ", " + "'" + defectDescription + "'" + ", " + category + ", " + "'" + finalDate + "'" + ")";
        
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(querySQL);
    
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("An error occurred while trying to insert products in the database.");
            e.printStackTrace();
        }
    }
}
    

