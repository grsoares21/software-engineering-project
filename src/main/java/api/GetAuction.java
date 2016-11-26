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

public class GetAuction extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String productId = req.getParameter("id");
        
        Connection conn = DatabaseHelper.getDatabaseConnection();
        
        String querySQL = "SELECT * FROM offered_products WHERE";
        querySQL = querySQL + " (id LIKE '%" + productId + "%')";
       
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            // consulta banco de produtos
            ResultSet productResult = stmt.executeQuery(querySQL);
            
            JSONObject productJSON = new JSONObject();
            
            // informações do produto
            productJSON.put("id", productResult.getInt("id"));
            productJSON.put("name", productResult.getString("name"));
            productJSON.put("description", productResult.getString("product_description"));
            productJSON.put("defectDescription", productResult.getString("defect_description"));
            productJSON.put("category", productResult.getInt("category"));
            productJSON.put("finalDate", productResult.getString("final_date"));

            int userId = productResult.getInt("offering_user_id");
            querySQL = "SELECT * FROM users WHERE";
            querySQL = querySQL + " (id = '%" + userId + "%')";
            // consulta banco de usuários
            ResultSet userResult = stmt.executeQuery(querySQL);

            // informações do usuário
            productJSON.put("user_id", userResult.getInt("id"));
            productJSON.put("username", userResult.getString("username"));
            productJSON.put("user_name", userResult.getString("name"));
            productJSON.put("email", userResult.getString("email"));

            querySQL = "SELECT * FROM products_photos WHERE";
            querySQL = querySQL + " (id LIKE '%" + productId + "%')";
            // consulta banco de fotos 
            ResultSet photosResult = stmt.executeQuery(querySQL);
            
            // informações das fotos: podemos ter mais de uma foto por produto
            JSONArray photosArray = new JSONArray();
            while (photosResult.next())
            {                
                photosArray.put(photosResult.getString("url"));
            }
            productJSON.put("photos_array", photosArray);
            
            resp.setContentType("application/json");
            resp.getOutputStream().print(productJSON.toString());
            
            stmt.close();
            conn.close();
            productResult.close();
        } catch (SQLException e) {
            System.err.println("An error occurred while trying to get auction information in the database.");
            e.printStackTrace();
        }

    }
}
