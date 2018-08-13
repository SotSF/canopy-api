package re.cretfi.se.api;

import java.net.SocketException;
import java.util.List;

import ch.bildspur.artnet.ArtNet;
import ch.bildspur.artnet.ArtNetException;
import ch.bildspur.artnet.ArtNetNode;
import ch.bildspur.artnet.events.ArtNetDiscoveryListener;
import ch.bildspur.artnet.packets.ArtDmxPacket;

public class ArtnetController implements ArtNetDiscoveryListener {

    private ArtNet artnet;

    public ArtnetController() {
        artnet = new ArtNet();
        this.connect();
    }

    public ArtNetNode controller;
    private int sequenceID;
    private int NUM_CHANNELS_PER_UNIVERSE = 512;

    private void connect() {
        try {
            artnet.start();
            artnet.getNodeDiscovery().addListener(this);
            artnet.startNodeDiscovery();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (ArtNetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void discoveredNewNode(ArtNetNode node) {
        if (controller == null) {
            controller = node;
            System.out.println("found controller");
        }
    }

    @Override
    public void discoveredNodeDisconnected(ArtNetNode node) {
        System.out.println("node disconnected: " + node);
        if (node == controller) {
            controller = null;
        }
    }

    @Override
    public void discoveryCompleted(List<ArtNetNode> nodes) {
        System.out.println(nodes.size() + " nodes found:");
        for (ArtNetNode n : nodes) {
            System.out.println(n);
        }
    }

    @Override
    public void discoveryFailed(Throwable t) {
        System.out.println("discovery failed");
    }

    private void test() {
        ArtNet artnet = new ArtNet();
        try {
            artnet.start();
            artnet.getNodeDiscovery().addListener(this);
            artnet.startNodeDiscovery();
            while (true) {
                if (controller != null) {
                    ArtDmxPacket dmx = new ArtDmxPacket();
                    dmx.setUniverse(controller.getSubNet(), controller.getDmxOuts()[0]);
                    dmx.setSequenceID(sequenceID % 255);
                    byte[] buffer = new byte[510];
                    for (int i = 0; i < buffer.length; i++) {
                        buffer[i] =
                                (byte) (Math.sin(sequenceID * 0.05 + i * 0.8) * 127 + 128);
                    }

                    dmx.setDMX(buffer, buffer.length);
                    artnet.unicastPacket(dmx, controller.getIPAddress());

                    dmx.setUniverse(controller.getSubNet(), controller.getDmxOuts()[1]);
                    artnet.unicastPacket(dmx, controller.getIPAddress());
                    sequenceID++;
                }
                Thread.sleep(30);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (ArtNetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendPacket (byte[] data, int universe) {
        ArtDmxPacket dmx = new ArtDmxPacket();
        dmx.setSubnetID(controller.getSubNet());
        dmx.setUniverseID(universe);
        dmx.setDMX(data, NUM_CHANNELS_PER_UNIVERSE);
        artnet.unicastPacket(dmx, controller.getIPAddress());
    }

    public void send(byte[] data) {
        // Iterate over the data and split it into DMX universes
        int numPixels = data.length / 3;

        byte[] buffer = new byte[NUM_CHANNELS_PER_UNIVERSE];
        int bufferSize = 0;
        int universe = 0;

        for (int i = 0; i < numPixels; i++) {
            // Add data to the buffer until it's filled
            if (bufferSize + 3 < NUM_CHANNELS_PER_UNIVERSE) {
                buffer[bufferSize] = data[i * 3];
                buffer[bufferSize + 1] = data[i * 3 + 1];
                buffer[bufferSize + 2] = data[i * 3 + 2];

                bufferSize += 3;
            } else {
                // Buffer is full, make a DMX packet and empty the buffer
                this.sendPacket(buffer, universe++);

                buffer = new byte[NUM_CHANNELS_PER_UNIVERSE];
                bufferSize = 0;
            }
        }

        // If the buffer is non-empty, send that data too
        if (bufferSize > 0) {
            this.sendPacket(buffer, universe);
        }
    }
}
