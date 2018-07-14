package re.cretfi.se.api;

import com.heroicrobot.dropbit.registry.DeviceRegistry;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class ApplicationConfig extends ResourceConfig {
    public ApplicationConfig(final DeviceRegistry registry) {
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(registry).to(DeviceRegistry.class);
            }
        });
    }
}
