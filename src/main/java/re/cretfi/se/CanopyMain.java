package re.cretfi.se;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class CanopyMain {
    public static void main(String[] args) throws Exception{

        // Create and start the web server
        System.out.println("Starting server.");
        Server server = new Server(7070);
        ServletContextHandler handler = new ServletContextHandler(server, "/");

        // hi java
        ServletHolder holder = new ServletHolder(new CanopyServlet("oh, hi"));
        handler.addServlet(holder, "/");
        server.start();
        System.out.println("Server Started");
    }
}
