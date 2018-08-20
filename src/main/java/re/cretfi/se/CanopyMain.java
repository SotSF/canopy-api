package re.cretfi.se;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heroicrobot.dropbit.registry.DeviceRegistry;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import re.cretfi.se.api.ApplicationConfig;


public class CanopyMain {
    public static void main(String[] args) throws Exception {

        int port = 8080;

        DeviceRegistry registry = new DeviceRegistry();
//        registry.setLogging(false);

        ResourceConfig config = new ApplicationConfig(registry);
        config.packages("re.cretfi.se.api"); // TODO: put this in the config??

        FilterHolder filterHolder = new FilterHolder(CrossOriginFilter.class);
        filterHolder.setInitParameter("allowedOrigins", "*");
        filterHolder.setInitParameter("allowedMethods", "GET, POST");

        ServletHolder canopyServlet
                = new ServletHolder(new ServletContainer(config));

        Server server = new Server(port);
        ServletContextHandler context
                = new ServletContextHandler(server, "/");

        context.addServlet(canopyServlet, "/api/*");
        context.addFilter(filterHolder, "/*", null);
        System.out.println("Starting server on port " + port);

        server.start();
        server.join();

    }
}
