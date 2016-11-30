package api;

import db.DatabaseHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

public class AcceptPurchase extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reportId = req.getParameter("reportId");
        String sessionToken = req.getSession().getAttribute("session_token").toString();

        Connection conn = DatabaseHelper.getDatabaseConnection();

        try {
            Statement buyerStmt = conn.createStatement();
            String getBuyerUserSQL = "SELECT * FROM users WHERE session_token='" + sessionToken + "'";
            ResultSet buyerUserResult = buyerStmt.executeQuery(getBuyerUserSQL);
            int buyerId = buyerUserResult.getInt("id");
            String buyerName = buyerUserResult.getString("name");
            String buyerEmail = buyerUserResult.getString("email");

            String getSellerUserSQL = "SELECT users.id, offered_products.name, users.name, users.email, offered_products.id FROM users, reports, offered_products" +
                    " WHERE reports.id =" + reportId + " AND reports.offered_products_id = offered_products.id AND " +
                    "users.id = offered_products.offering_user_id";

            Statement sellerStmt = conn.createStatement();
            ResultSet sellerUserResult = sellerStmt.executeQuery(getSellerUserSQL);
            int sellerId = sellerUserResult.getInt(1);
            String productName = sellerUserResult.getString(2);
            String sellerName = sellerUserResult.getString(3);
            String sellerEmail = sellerUserResult.getString(4);
            int productId = sellerUserResult.getInt(5);

            String buyerReportText = "Voce aceitou a compra do produto: " + productName + ". O nome do vendedor do " +
                    "produto e " + sellerName + " e seu e-mail e " + sellerEmail + ". Por favor confirme o recebimento do" +
                    " produto assim que recebe-lo.";

            String sellerReportText = "Voce vendeu o produto: " + productName + ". O nome do comprador do " +
                    "produto e" + buyerName + " e seu e-mail e " + buyerEmail + ". Por favor contate o comprador para" +
                    " discutir o encaminhamento do produto.";

            Statement reportStmts = conn.createStatement();
            String insertBuyerPurchaseReportSQL = "INSERT INTO reports (subject_user, date, type, text, offered_products_id, object_user)"
                    + " VALUES (" + buyerId
                    + ", " + new Date(new java.util.Date().getTime()).toString()
                    + ", 2"
                    + ", '" + buyerReportText + "'"
                    + ", " + productId
                    + ", " + sellerId +")";
            reportStmts.executeUpdate(insertBuyerPurchaseReportSQL);

            String insertSellerPurchaseReportSQL = "INSERT INTO reports (subject_user, date, type, text, offered_products_id, object_user)"
                    + " VALUES (" + sellerId
                    + ", " + new Date(new java.util.Date().getTime()).toString()
                    + ", 3"
                    + ", '" + sellerReportText + "'"
                    + ", " + productId
                    + ", " + buyerId + ")";
            reportStmts.executeUpdate(insertSellerPurchaseReportSQL);

            String deleteWinningBidReportSQL = "DELETE FROM reports WHERE id=" + reportId;
            reportStmts.executeUpdate(deleteWinningBidReportSQL);

            sellerStmt.close();
            buyerStmt.close();
            reportStmts.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("An error occurred while user was trying to accept a purchase.");
            e.printStackTrace();
        }
    }
}
