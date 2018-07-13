package re.cretfi.se;

import com.heroicrobot.dropbit.registry.DeviceRegistry;
import javafx.beans.Observable;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Observer;

public class CanopyServlet extends HttpServlet implements Observer {

    public boolean hasStrips = false;

    // private
    DeviceRegistry registry;

    CanopyServlet() {
        this.registry = new DeviceRegistry();
        initialize();
    }

    public CanopyServlet(DeviceRegistry registry) {
        this.registry = registry;
        initialize();
    }

    private void initialize() {
        registry.addObserver(this);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setStatus(HttpStatus.OK_200);
        if (this.hasStrips) {
            resp.getWriter().println("I have strips!");
        } else {
            resp.getWriter().println("I do not have strips");
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
