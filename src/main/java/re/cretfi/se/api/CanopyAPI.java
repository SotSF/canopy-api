package re.cretfi.se.api;

import com.heroicrobot.dropbit.devices.pixelpusher.Pixel;
import com.heroicrobot.dropbit.devices.pixelpusher.Strip;
import com.heroicrobot.dropbit.registry.DeviceRegistry;
import re.cretfi.se.api.response.RegistryStats;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api")
public class CanopyAPI {

    private DeviceRegistry registry;
    boolean pushing;

    @Inject
    CanopyAPI(DeviceRegistry registry) {
        this.registry = registry;
    }

    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public String ping() {
        return "\"pong\"";
    }

    @GET
    @Path("/stats")
    public Response stats() {
        return Response.ok(new RegistryStats(registry, pushing)).build();
    }

    @GET
    @Path("/start")
    public boolean start() {
        registry.startPushing();
        pushing = true;
        return true;
    }

    @GET
    @Path("/stop")
    public boolean stop() {
        registry.stopPushing();
        pushing = false;
        return false;
    }

    @GET
    @Path("/push")
    public boolean push() {
        List<Strip> strips = registry.getStrips();
        Pixel pixel = new Pixel();
        pixel.red = (byte)0x33;
        for (Strip strip : strips) {
            for (int i = 0; i < strip.getLength(); i++){
                System.out.println("Setting pixel...");
                strip.setPixel(pixel, i);
            }
        }
        return true;
    }

    @GET
    @Path("/clear")
    public boolean clear() {
        List<Strip> strips = registry.getStrips();
        Pixel pixel = new Pixel(0);
        for (Strip strip : strips) {
            for (int i = 0; i < strip.getLength(); i++){
                System.out.println("Setting pixel...");
                strip.setPixel(pixel, i);
            }
        }
        return true;
    }
}