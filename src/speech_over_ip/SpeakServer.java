package speech_over_ip;

import java.io.IOException;

import data.Configuration;

/**
 * Controls the thread that listens on the socket and plays sound.
 * 
 * @author Adam Veres <adveres>
 * 
 */
public class SpeakServer {
    private Configuration config;
    private Receiver receiverThread;

    public SpeakServer(Configuration config) {
        this.config = config;
    }

    /**
     * Listen on the socket and play bytes we receive as audio
     */
    public void listen() {
        try {
            receiverThread = new Receiver(config);
            receiverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Tell the thread to stop looping
     */
    public void stopListening() {
        if (null == receiverThread) {
            System.out.println("Not listening anyways!");
        } else {
            this.receiverThread.stopListening();
        }
    }

}
