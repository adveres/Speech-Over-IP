package speech_over_ip;

import java.net.*;
import java.util.Random;

import data.Configuration;
import utilities.Utils;

public class SpeakServer {
    private Configuration config;
    Random rand = new Random();

    public SpeakServer(Configuration config) {
        this.config = config;
    }

    /**
     * Listen on the socket and play bytes we receive as audio
     */
    public void listen() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(config.getPort());
            byte[] receiveData = new byte[Utils.latencyToBytes(config.getLatencyInMS())];

            while (true) {
                DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(packet);

                // Randomly drop the packet if we're given a probability to do
                // so.
                if (shouldDropThisPacket()) {
                    continue;
                }

                // Otherwise play them as sound.
                try {
                    byte audioData[] = packet.getData();

                    AudioBytePlayer ap = new AudioBytePlayer(audioData);
                    ap.start();

                } catch (Exception e) {
                    System.out.println(e);
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Determine whether or not to drop this packet based on loss percentage
     * given
     * 
     * @return
     */
    private boolean shouldDropThisPacket() {
        int rnd = rand.nextInt(100); // Generates from 0-99
        rnd += 1;// Want 1-100

        if (rnd < config.getLossPercent()) {
            return true;
        }
        return false;
    }
}
