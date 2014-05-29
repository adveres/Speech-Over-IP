package speech_over_ip;

import java.net.*;
import java.util.Random;

public class SpeakServer {
    private int PORT = 0;
    private int LATENCY = 0;
    private int LOSS = 0;
    Random rand = new Random();

    public SpeakServer(int port, int latency, int loss) {
        PORT = port;
        LATENCY = latency;
        LOSS = loss;
    }

    /**
     * Listen on the socket and play bytes we receive as audio
     */
    public void listen() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            byte[] receiveData = new byte[Utils.latencyToBytes(LATENCY)];

            while (true) {
                DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(packet);

                // Randomly drop the packet if we're given a probability to do
                // so.
                if (shouldDropThisPacket()) {
                    continue;
                }

                //Otherwise play them as sound.
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

        if (rnd < this.LOSS) {
            return true;
        }
        return false;
    }
}
