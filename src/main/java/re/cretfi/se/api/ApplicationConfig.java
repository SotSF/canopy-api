package re.cretfi.se.api;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

import re.cretfi.se.api.ArtnetController;

@ApplicationPath("/api")
public class ApplicationConfig extends ResourceConfig {
    public ApplicationConfig(final ArtnetController controller) {
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(controller).to(ArtnetController.class);
            }
        });
    }
}
