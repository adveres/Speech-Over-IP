package speech_over_ip;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * @author Adam Veres <adveres>
 * 
 */
public class Sender {

    Configuration config = null;

    // TCP
    private Socket senderSocket = null;
    OutputStream out = null;
    DataOutputStream dos = null;

    // UDP
    DatagramSocket clientSocket = null;
    InetAddress ipAddress = null;

    public Sender(Configuration config) {
        this.config = config;

        if (config.isPacketTypeTCP()) {
            this.initTCP();
        } else if (config.isPacketTypeUDP()) {
            this.initUDP();
        } else {
            System.err.println("Invalid packet type given to Sender.");
        }
    }

    /**
     * Init TCP sockets and output streams
     */
    private void initTCP() {
        try {
            senderSocket = new Socket(config.getHost(), config.getPort());
            out = senderSocket.getOutputStream();
            dos = new DataOutputStream(out);
        } catch (UnknownHostException e) {
            System.err.println("Unable to find host: " + config.getHost());
        } catch (IOException e) {
            System.err.println("Couldn't get I/O connection to: " + config.getHost());
        }
    }

    /**
     * Init UDP socket and ip addr object
     */
    private void initUDP() {
        try {
            clientSocket = new DatagramSocket();
            ipAddress = InetAddress.getByName(config.getHost());
        } catch (UnknownHostException e) {
            System.err.println("Unable to find host: " + config.getHost());
        } catch (IOException e) {
            System.err.println("Couldn't get I/O connection to: " + config.getHost());
        }
    }

    /**
     * Send an array of bytes over a socket
     * 
     * @param audioBytes
     */
    public void sendBytes(byte[] audioBytes) {
        if (config.isSpeechDetectionOn() && shouldDropAudioBasedOnSilence(audioBytes)) {
            return;
        } else {
            System.out.println("Transmit audio | energy = " + Utils.energyOfChunk(audioBytes));
        }

        if (this.config.isPacketTypeTCP()) {
            this.writeToTCP(audioBytes);
        } else if (this.config.isPacketTypeUDP()) {
            this.writeToUDP(audioBytes);
        } else {
            System.err.println("Unsupported packet type: " + this.config.getPacketType()
                    + ". Unable to send.");
        }
    }

    /**
     * Takes byte array and sends over TCP
     * 
     * @param audioBytes
     */
    public void writeToTCP(byte[] audioBytes) {
        try {
            // Write array length so receiver can allocate a buffer of the
            // correct size. Then send actual data.
            dos.writeInt(audioBytes.length);
            if (audioBytes.length > 0) {
                dos.write(audioBytes);
            }
        } catch (IOException e) {
            System.out.println("There was an error writing to the socket: " + e);
        }
    }

    /**
     * Takes byte array and sends over UDP
     * 
     * @param audioBytes
     */
    private void writeToUDP(byte[] audioBytes) {
        DatagramPacket sendPacket = new DatagramPacket(audioBytes, audioBytes.length, ipAddress,
                config.getPort());
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given an array of audio bytes, is the energy over ITU and worth being
     * sent?
     * 
     * @param audioBytes
     * @return
     */
    private boolean shouldDropAudioBasedOnSilence(byte[] audioBytes) {
        int energy = Utils.energyOfChunk(audioBytes);
        if (energy <= config.getSpeechConfig().getITU()) {
            return true;
        }

        // TCP was a real piece of work. It was making this reverberating
        // clicking noise, so I needed to up the threshold a bit to stop it from
        // constantly clicking forever.
        if (config.isPacketTypeTCP() && energy <= config.getSpeechConfig().getITU() + 100) {
            return true;
        }

        return false;
    }
}
