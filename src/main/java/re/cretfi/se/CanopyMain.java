package re.cretfi.se;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class CanopyMain {
    public static void main(String[] args) throws Exception{
        System.out.println("Starting server.");
        Server server = new Server(7070);
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        handler.addServlet(CanopyServlet.class, "/");
        server.start();
    }
}
