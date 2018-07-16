package re.cretfi.se.api;

import com.heroicrobot.dropbit.devices.pixelpusher.Pixel;
import com.heroicrobot.dropbit.devices.pixelpusher.Strip;
import com.heroicrobot.dropbit.registry.DeviceRegistry;
import re.cretfi.se.api.response.RegistryStats;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.List;
import java.util.Observer;

@Path("/api")
public class CanopyAPI implements Observer {

    private DeviceRegistry registry;

    boolean pushing;

    @Inject
    CanopyAPI(DeviceRegistry registry) {
        this.registry = registry;
        this.pushing = true;
        registry.startPushing();
        registry.addObserver(this);
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
                strip.setPixel(pixel, i);
            }
        }
        return true;
    }

    @POST
    @Path("/render")
    public String render(String requestData) {
        byte[] data = Base64.getDecoder().decode(requestData);

        if (data.length % 3 != 0)
            return "invalid pixel array length";

        List<Strip> strips = registry.getStrips();
        Pixel p = new Pixel();

        System.out.println("Got data:" + data);

        int i = 0;
        striploop:
        for (Strip strip : strips) {

            Pixel[] pixels = new Pixel[strip.getLength()];

            for (int j=0; j < strip.getLength(); j++) {
                if (i >= data.length-2)
                    break striploop;
                pixels[j] = new Pixel(data[i], data[i+1], data[i+2]);
                i += 3;
            }
            strip.setPixels(pixels);
        }
        return "yay\n";
    }

    @POST
    @Path("/echo")
    public String echo(String data) {
        return data;
    }

    /**
     * @param registry
     * @param updatedDevice
     *
     * gets called by DeviceRegistry whenever pushers are added/removed, etc.
     */
    @Override
    public void update(java.util.Observable registry, Object updatedDevice) {
        // For whatever reason, storing and reusing the strips from here causes a silent hang.
        // this.strips = this.registry.getStrips();
    }
}