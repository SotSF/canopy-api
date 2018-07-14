package re.cretfi.se;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heroicrobot.dropbit.registry.DeviceRegistry;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import re.cretfi.se.api.ApplicationConfig;

public class CanopyMain {
    public static void main(String[] args) throws Exception {

        DeviceRegistry registry = new DeviceRegistry();

        ResourceConfig config = new ApplicationConfig(registry);
        config.packages("re.cretfi.se.api"); // TODO: put this in the config??

        ServletHolder jerseyServlet
                = new ServletHolder(new ServletContainer(config));

        Server server = new Server(8080);
        ServletContextHandler context
                = new ServletContextHandler(server, "/");
        context.addServlet(jerseyServlet, "/*");
        server.start();
        server.join();

    }
    public static void mainAnother(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(9999);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "re.cretfi.se.api");

        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jettyServer.destroy();
        }

    }

    public static void mainOld(String[] args) throws Exception{

        // Create and start the web server
        System.out.println("Starting server.");
        Server server = new Server(7070);
        ServletContextHandler handler = new ServletContextHandler(server, "/");

        GsonBuilder gson = new GsonBuilder();
        DeviceRegistry registry = new DeviceRegistry();

        ServletHolder holder = new ServletHolder(new CanopyServlet(registry, gson));
        handler.addServlet(holder, "/");
        server.start();
        System.out.println("Server Started");
    }
}
