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
import java.util.*;

@Path("/api")
public class CanopyAPI implements Observer {

    private DeviceRegistry registry;

    byte[] renderBuffer = new byte[0];
    boolean pushing;

    @Inject
    CanopyAPI(DeviceRegistry registry) {
        this.registry = registry;
        this.pushing = true;
        registry.startPushing();
        registry.addObserver(this);
//        registry.setFrameCallback(this, "flush");
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
        Optional<Integer> pixelCount = registry.getStrips()
                .stream()
                .map(strip -> strip.getLength())
                .reduce((count, length) -> count + length);

        byte[] pixels = new byte[pixelCount.orElse(0)];

        this.renderBuffer = pixels;
        flush();
        return true;
    }

    @POST
    @Path("/render")
    public String render(String requestData) {
        byte[] data = new byte[0];
        try{
            data = Base64.getDecoder().decode(requestData);
        } catch (IllegalArgumentException e){
            System.out.println("Caught bad b64 string: " + requestData);
        }

        if (data.length % 3 != 0)
            return "invalid pixel array length";

        this.renderBuffer = data;
        flush();

        return "yay\n";
    }

    @POST
    @Path("/renderbytes")
    public String renderbytes(String requestData) {
        byte[] data = requestData.getBytes();

        if (data.length % 3 != 0)
            return "invalid pixel array length";

        this.renderBuffer = data;
        flush();

        return "yay\n";
    }

    @POST
    @Path("/renderbytesswizzled")
    public String renderbytesswizzled(String requestData) {
        byte[] data = requestData.getBytes();

        if (data.length % 3 != 0)
            return "invalid pixel array length";

        this.renderBuffer = data;
        flush(true);

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
//        strips = this.registry.getStrips();
    }

    public void flush(){
        flush(false);
    }

    public void flush(boolean swizzle) {
        List<Strip> strips = registry.getStrips();
        int bufferLength = renderBuffer.length;
        for (Strip strip : strips) {
            int stripLength = strip.getLength();
            Pixel[] pixels = new Pixel[stripLength];

            for (int j = 0, i = 0;
                 j < stripLength && i < bufferLength - 2;
                 j++, i += 3) {
                boolean swizzMe = (j/ 75) % 2 == 0;
                int index;

                //Reverse order of every other block of 75 pixels for physical strip configuration
                if (swizzle && swizzMe) {
                    index = i - (i % 75) + (75 - i % 75) -1;
                } else {
                    index = i;
                }
                pixels[j] = new Pixel(renderBuffer[index], renderBuffer[index + 1], renderBuffer[index + 2]);
//                strip.setPixel(pixel, j);
            }
            strip.setPixels(pixels);
        }
    }
}