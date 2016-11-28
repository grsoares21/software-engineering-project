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
        querySQL = querySQL + " id = " + productId;
       
        Statement stmt;
        try {
            stmt = conn.createStatement();
            // consulta banco de produtos
            ResultSet productResult = stmt.executeQuery(querySQL);
            
            JSONObject productJSON = new JSONObject();

            if(productResult.next()) {
                // informacoes do produto
                productJSON.put("name", productResult.getString("name"));
                productJSON.put("description", productResult.getString("description"));
                productJSON.put("defectDescription", productResult.getString("defect_description"));
                productJSON.put("category", productResult.getInt("category"));
                productJSON.put("finalDate", productResult.getString("final_date"));

                int userId = productResult.getInt("offering_user_id");
                querySQL = "SELECT * FROM users WHERE";
                querySQL = querySQL + " id = " + userId;
                // consulta banco de usuarios
                ResultSet userResult = stmt.executeQuery(querySQL);

                if(userResult.next()) {
                    // informacoes do usuario
                    productJSON.put("user_id", userResult.getInt("id"));
                    productJSON.put("username", userResult.getString("username"));
                    productJSON.put("user_name", userResult.getString("name"));
                    productJSON.put("email", userResult.getString("email"));
                }

                //consulta lances para saber o preço
                querySQL = "SELECT MAX(bid_value) FROM bids WHERE offered_product_id = " + productId;
                ResultSet bidsResult = stmt.executeQuery(querySQL);
                float price = 0;
                if(bidsResult.next()) {
                    price = bidsResult.getFloat(1);
                }
                productJSON.put("price", price);

                //consulta fotos
                querySQL = "SELECT * FROM product_photos WHERE";
                querySQL = querySQL + " offered_product_id = " + productId;
                ResultSet photosResult = stmt.executeQuery(querySQL);
                // informacoes das fotos: podemos ter mais de uma foto por produto
                JSONArray photosArray = new JSONArray();
                while (photosResult.next()) {
                    photosArray.put(photosResult.getString("url"));
                }
                productJSON.put("photosArray", photosArray);
            }
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
