package re.cretfi.se.api;

import com.heroicrobot.dropbit.registry.DeviceRegistry;
import re.cretfi.se.api.response.RegistryStats;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/api")
public class CanopyAPI {

    private DeviceRegistry registry;

    @Inject
    CanopyAPI(DeviceRegistry registry) {
        this.registry = registry;
    }

    @GET
    @Path("/ping")
    public String ping() {
        return "pong";
    }

    @GET
    @Path("/stats")
    public Response stats() {
        return Response.ok(new RegistryStats(registry)).build();
    }

    @GET
    @Path("/hello")
    public String hello() {
        return "world";
    }
}