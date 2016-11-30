package api;

import db.DatabaseHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

public class ConfirmReception extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String buyerReportId = req.getParameter("reportId");
        String sessionToken = req.getSession().getAttribute("session_token").toString();

        Connection conn = DatabaseHelper.getDatabaseConnection();

        try {
            Statement stmt = conn.createStatement();
            String getBuyerUserSQL = "SELECT * FROM users WHERE session_token='" + sessionToken + "'";
            ResultSet buyerUserResult = stmt.executeQuery(getBuyerUserSQL);
            String buyerName = buyerUserResult.getString("name");
            int buyerId = buyerUserResult.getInt("id");

            String getBuyerReportSQL = "SELECT * FROM reports WHERE id=" + buyerReportId;
            ResultSet buyerReportResult = stmt.executeQuery(getBuyerReportSQL);
            int productId = buyerReportResult.getInt("offered_products_id");
            int sellerId = buyerReportResult.getInt("object_user");

            String getProductSQL = "SELECT * FROM offered_products WHERE id=" + productId;
            ResultSet productResult = stmt.executeQuery(getProductSQL);
            String productName = productResult.getString("name");

            String getSellerReportSQL = "SELECT * FROM reports WHERE offered_products_id=" + productId + " AND " +
                    "type=3 AND subject_user=" + sellerId + " AND object_user=" + buyerId;
            ResultSet sellerReportResult = stmt.executeQuery(getSellerReportSQL);
            int sellerReportId = sellerReportResult.getInt("id");

            String newSellerReportText = "O usuário " + buyerName + " recebeu o seu produto: " + productName;
            String newBuyerReportText = "Você recebeu com sucesso o produto " + productName;

            String updateSellerReportSQL = "UPDATE reports SET text='" + newSellerReportText + "' WHERE id=" + sellerReportId;
            String updateBuyerReportSQL = "UPDATE reports SET text='" + newBuyerReportText+ "', type=4 WHERE id=" + buyerReportId;

            stmt.executeUpdate(updateSellerReportSQL);
            stmt.executeUpdate(updateBuyerReportSQL);

            buyerUserResult.close();
            buyerReportResult.close();
            productResult.close();
            sellerReportResult.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("An error occurred while user was trying to confirm the reception of a product.");
            e.printStackTrace();
        }
    }
}
