package re.cretfi.se;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heroicrobot.dropbit.registry.DeviceRegistry;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import re.cretfi.se.api.ApplicationConfig;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

public class CanopyMain {
    public static void main(String[] args) throws Exception {

        int port = 8080;
        Server server = new Server(port);
        /*
         * Static Server
         */

        ServletContextHandler resourceContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ResourceHandler resourceHandler = new ResourceHandler();

        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "index.html" });

        resourceHandler.setResourceBase("./static");

        resourceContext.setHandler(resourceHandler);
        resourceContext.setContextPath("/static");

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
