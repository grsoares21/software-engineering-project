package api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

public class TestRequest extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String parameter = req.getParameter("texto");
        System.out.println("Acabo de receber uma request HTTP do tipo Get, onde o parâmetro 'texto' foi: " + parameter);

        Random ran = new Random();
        Integer randomNumber = ran.nextInt();
        System.out.println("Irei responder o número aleatório: " + randomNumber);
        resp.addHeader("numero", randomNumber.toString());
    }
}
