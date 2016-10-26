import db.DatabaseHelper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebApp {
    public static void main(String args[]) throws Exception {
        Server server = new Server(8080); //initializes a web jetty server in port 8080

        WebAppContext webapp = new WebAppContext();
        webapp.setResourceBase("src/main/resources/");
        webapp.setOverrideDescriptor("src/main/resources/webapp/WEB-INF/web.xml");
        webapp.setContextPath("/"); //set root URL to access this context

        DatabaseHelper.initializeDatabase();
        server.setHandler(webapp);
        server.start();
        server.join();
    }
}
