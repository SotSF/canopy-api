package re.cretfi.se;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.heroicrobot.dropbit.devices.pixelpusher.Pixel;
import com.heroicrobot.dropbit.devices.pixelpusher.PixelPusher;
import com.heroicrobot.dropbit.devices.pixelpusher.Strip;
import com.heroicrobot.dropbit.registry.DeviceRegistry;
import javafx.beans.Observable;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class CanopyServlet extends HttpServlet implements Observer {

    private GsonBuilder gson;
    public boolean hasStrips = false;

    // private
    DeviceRegistry registry;
    private boolean pushing;

    CanopyServlet(DeviceRegistry registry, GsonBuilder gson) {
        this.registry = registry;
        this.gson = gson;
        initialize();
    }

    private void initialize() {
        registry.addObserver(this);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        // Dispatch the request
        System.out.println(req.getRequestURI());
        String uri = req.getRequestURI();
        if (uri.equalsIgnoreCase("/has_strips"))
            stripCount(req, resp);
        else if (uri.equalsIgnoreCase("/stats"))
            stats(req, resp);
        else if (uri.equalsIgnoreCase("/start_pushing"))
            start_pushing(req, resp);
        else if (uri.equalsIgnoreCase("/stop_pushing"))
            stop_pushing(req, resp);
        else if (uri.equalsIgnoreCase("/push"))
            push_pixel(req, resp);

    }

    private void push_pixel(HttpServletRequest req, HttpServletResponse resp) {
        List<Strip> strips = registry.getStrips();
        Pixel pixel = new Pixel();
        pixel.red = (byte)0xff;
        for (Strip strip : strips) {
            for (int i = 0; i < strip.getLength(); i++){
                System.out.println("Setting pixel...");
                strip.setPixel(pixel, i);
            }

        }
    }

    private void start_pushing(HttpServletRequest req, HttpServletResponse resp) {
        registry.startPushing();
        pushing = true;
    }

    private void stop_pushing(HttpServletRequest req, HttpServletResponse resp) {
        registry.stopPushing();
        pushing = false;
    }

    private void stats(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        JsonObject stats = new JsonObject();
        stats.addProperty("pushing", this.pushing);
        stats.addProperty("hello", "world");
        stats.addProperty("frameLimit", registry.getFrameLimit());
        stats.addProperty("logEnabled", registry.getLogging());
        stats.addProperty("totalPower", registry.getTotalPower());
        stats.addProperty("totalPowerLimit", registry.getTotalPowerLimit());
        stats.addProperty("powerScale", registry.getPowerScale());

        JsonArray pushers = new JsonArray();

        System.out.println("pushermap: " + registry.getPusherMap());

        // This blows up at runtime with NullPointerException for some reason.
        for (PixelPusher p : registry.getPusherMap().values()) {
            JsonObject pusher = new JsonObject();
            pusher.addProperty("numberOfStrips", p.getNumberOfStrips());
            pusher.addProperty("maxStripsPerPacket", p.getMaxStripsPerPacket());
            pusher.addProperty("pixelsPerStrip", p.getPixelsPerStrip());
            pusher.addProperty("pusherFlags", p.getPusherFlags());
            pusher.addProperty("updatePeriod", p.getUpdatePeriod());
            pusher.addProperty("powerTotal", p.getPowerTotal());
            pusher.addProperty("deltaSequence", p.getDeltaSequence());
            pusher.addProperty("extraDelay", p.getExtraDelay());
            pushers.add(pusher);
        }

        stats.add("pushers", pushers);

        resp.getWriter().println(stats.toString());
        resp.setStatus(HttpStatus.OK_200);
    }

    private void stripCount(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setStatus(HttpStatus.OK_200);
        if (this.hasStrips) {
            resp.getWriter().println("true");
        } else {
            resp.getWriter().println("false");
        }

    }

    public void update(java.util.Observable registry, Object updatedDevice) {
        System.out.println("Registry update detected");
        if (updatedDevice != null) {
            System.out.println("Device change: " + updatedDevice);
        }
        this.hasStrips = true;
    }
}
