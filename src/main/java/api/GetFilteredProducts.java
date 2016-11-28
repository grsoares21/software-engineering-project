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

public class GetFilteredProducts extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String searchQuery = req.getParameter("searchQuery");
        String category = req.getParameter("category");

        Date currentDate = new Date(new java.util.Date().getTime());

        Connection conn = DatabaseHelper.getDatabaseConnection();
        String querySQL = "SELECT * FROM offered_products WHERE";

        querySQL = querySQL + " (name LIKE '%" + searchQuery + "%' OR " +
                    "description LIKE '%" + searchQuery + "%' OR " +
                    "defect_description LIKE '%" + searchQuery + "%')";

        if(category != null) {
            querySQL = querySQL + " AND category = " + category;
        }

        querySQL = querySQL + " AND final_date > '" + currentDate.toString() + "'";

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(querySQL);
            JSONArray responseArray = new JSONArray();

            while(result.next()) {
                JSONObject currentProductJSON = new JSONObject();

                int currentProductId = result.getInt("id");
                currentProductJSON.put("id", currentProductId);
                currentProductJSON.put("name", result.getString("name"));
                currentProductJSON.put("description", result.getString("description"));
                currentProductJSON.put("defectDescription", result.getString("defect_description"));
                currentProductJSON.put("category", result.getInt("category"));
                currentProductJSON.put("finalDate", result.getString("final_date"));

                //consulta fotos
                querySQL = "SELECT * FROM product_photos WHERE";
                querySQL = querySQL + " offered_product_id = " + currentProductId;

                Statement photoStmt = conn.createStatement();
                ResultSet photosResult = photoStmt.executeQuery(querySQL);
                if (photosResult.next()) {
                    currentProductJSON.put("photoUrl", photosResult.getString("url"));
                }
                photoStmt.close();
                photosResult.close();
                responseArray.put(currentProductJSON);
            }
            resp.setContentType("application/json");
            resp.getOutputStream().print(responseArray.toString());

            stmt.close();
            conn.close();
            result.close();
        } catch (SQLException e) {
            System.err.println("An error occurred while trying to search for products in the database.");
            e.printStackTrace();
        }
    }
}
