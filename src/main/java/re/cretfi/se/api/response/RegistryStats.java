package re.cretfi.se.api.response;

import com.heroicrobot.dropbit.devices.pixelpusher.PixelPusher;
import com.heroicrobot.dropbit.registry.DeviceRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistryStats {

    private final double powerScale;
    private final int frameLimit;
    private final boolean logEnabled;
    private final long totalPower;
    private final long totalPowerLimit;
    private final Pusher[] pushers;

    public RegistryStats(DeviceRegistry registry) {
        frameLimit = registry.getFrameLimit();
        logEnabled = registry.getLogging();
        totalPower = registry.getTotalPower();
        totalPowerLimit = registry.getTotalPowerLimit();
        powerScale = registry.getPowerScale();

        List<PixelPusher> pps = registry.getPushers();
        pushers = new Pusher[pps.size()];
        for (int i=0; i < pps.size(); i++) {
            pushers[i] = new Pusher(pps.get(i));
        }
    }

    private class Pusher {
        private final int numberOfStrips;
        private final int maxStripsPerPacket;
        private final int pixelsPerStrip;
        private final long pusherFlags;
        private final long updatePeriod;
        private final long powerTotal;
        private final long deltaSequence;
        private final long extraDelay;

        Pusher(PixelPusher pp) {
            numberOfStrips = pp.getNumberOfStrips();
            maxStripsPerPacket = pp.getMaxStripsPerPacket();
            pixelsPerStrip = pp.getPixelsPerStrip();
            pusherFlags = pp.getPusherFlags();
            updatePeriod = pp.getUpdatePeriod();
            powerTotal = pp.getPowerTotal();
            deltaSequence = pp.getDeltaSequence();
            extraDelay = pp.getExtraDelay();
        }
    }
}