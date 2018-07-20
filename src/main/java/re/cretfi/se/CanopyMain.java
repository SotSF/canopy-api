package re.cretfi.se;

import com.heroicrobot.dropbit.registry.DeviceRegistry;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.Scanner;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import re.cretfi.se.api.ApplicationConfig;

import java.net.URI;
import java.net.URL;

public class CanopyMain {
    public static void main(String[] args) throws Exception {

        int port = 8080;
        Server server = new Server(port);

        /*
         * Canopy API
         */

        DeviceRegistry registry = new DeviceRegistry();
//        registry.setLogging(false);

        ResourceConfig config = new ApplicationConfig(registry);
        config.packages("re.cretfi.se.api"); // TODO: put this in the config??

        ServletHolder canopyServlet
                = new ServletHolder(new ServletContainer(config));

        ServletContextHandler canopyContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        canopyContext.setContextPath("/api");
        canopyContext.addServlet(canopyServlet, "/*");

        /*
         * Static Server
         *
         * In the project, finds files under src/main/java/resources/static/
         */

        URL webRootLocation = CanopyMain.class.getClass().getResource("/static/index.html");

        URI webRootUri = URI.create(webRootLocation.toURI().toASCIIString().replaceFirst("/index.html$","/"));

        System.out.println("webRootLocation: " + webRootLocation);
        System.out.println("webRootUri: " + webRootUri);

        ServletContextHandler resourceContext = new ServletContextHandler();
        resourceContext.setContextPath("/");
        resourceContext.setBaseResource(Resource.newResource(webRootUri));
        resourceContext.setWelcomeFiles(new String[] { "index.html" });

        resourceContext.addServlet(DefaultServlet.class, "/");

        /*
         * Run
         */
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceContext, canopyContext, new DefaultHandler() });

        server.setHandler(handlers);

        System.out.println("Starting server on port " + port);

        server.start();
        server.join();

    }
}
