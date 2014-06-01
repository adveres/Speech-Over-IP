package speech_over_ip;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Receiver extends Thread {

    private Configuration config;
    private Random rand = new Random();
    
    AudioBytePlayer audioBytePlayer;

    boolean listening = true;

    public Receiver(Configuration config) throws IOException {
        this.config = config;
        audioBytePlayer = new AudioBytePlayer();
    }

    public void run() {
        this.listen();
    }

    /**
     * Tell the thread to stop listening on socket.
     */
    public void stopListening() {
        this.listening = false;
    }

    /**
     * Listen on the socket and play bytes we receive as audio
     */
    private void listen() {
        if (config.isPacketTypeUDP()) {
            this.listenOnUDP();
        } else if (config.isPacketTypeTCP()) {
            this.listenOnTCP();
        } else {
            System.err.println("Invalid packet type: " + config.getPacketType()
                    + " given to listener");
        }
    }

    /**
     * Listen on TCP socket and play audio packets received
     */
    private void listenOnTCP() {
        ServerSocket serverSocket = null;
        InputStream is = null;
        DataInputStream dis = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(config.getPort());
            socket = serverSocket.accept();
        } catch (IOException ex) {
            System.out.println("Unable to accept client connection.");
        }

        System.out.println("Accepted connection: " + socket);

        try {
            is = socket.getInputStream();
            dis = new DataInputStream(is);
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream.");
            System.exit(1);
        }

        try {
            int count = 0;
            int bufferSize = 0;
            byte[] receivedData = null;

            while (listening) {

                // :::NOTE:::
                // dis.readInt() was spewing garbage all over the audio line. I
                // stopped using it because it sounded so awful. I get much
                // better sound from simply allocating the right sized array
                // myself.
                //
                // bufferSize = dis.readInt();
                //

                bufferSize = Utils.latencyToBytes(config.getLatencyInMS());
                if (bufferSize < 0 || bufferSize > Utils.latencyToBytes(Constants.CHUNK_OF_1000MS)) {
                    // Sometimes a junk integer gets read, and must be
                    // discarded.
                    continue;
                }
                receivedData = new byte[bufferSize];

                count = dis.read(receivedData);
                if (count > 0) {
                    this.playPacket(receivedData);
                }
            }

            is.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.err
                    .println("An exception occurred during listening on TCP. Did the other client quit?");
            System.err.println(e);
            System.exit(1);
        }
    }

    /**
     * Listen on UDP socket and play audio packets received
     */
    private void listenOnUDP() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(config.getPort());
            byte[] receiveData = new byte[Utils.latencyToBytes(config.getLatencyInMS())];

            while (this.listening) {
                DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(packet);
                byte audioData[] = packet.getData();
                this.playPacket(audioData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Plays audio from the packet if the packet is not "dropped".
     * 
     * @param audioData
     */
    private void playPacket(byte[] audioData) {
        // Randomly drop the packet if we're given a probability to do so.
        if (shouldDropThisPacket()) {
            return;
        }

        // Otherwise play them as sound.
        try {
            audioBytePlayer.playBytes(audioData);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
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
