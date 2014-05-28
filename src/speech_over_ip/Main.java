package speech_over_ip;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Loss: 0%, 1%, 5%, 10%, 20% Latency (by increasing the sample interval): 40ms,
 * 100ms, 250ms, 500ms, 1000ms Connection type: TCP, UDP Speech detection: on,
 * off
 * 
 * @author Adam Veres <adveres>
 * 
 */
public class Main {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println(usage());
            System.exit(1);
        }

        int loss = Integer.parseInt(args[0]);
        int latency = Integer.parseInt(args[1]);
        String packetType = args[2];
        String detectSpeech = args[3];

        // String host = "192.168.1.11";
        // String host = "localhost";
        String host = "127.0.0.1";
        int port = 6222;

        Receiver receiver = null;
        Sender sender = null;

        byte[] audioData = FileSaver.fileToBytes(Utils.FILENAME_SOUND_RAW);
        System.out.println("Main audio size: " + audioData.length);

        try {
            receiver = new Receiver(port);
            receiver.start();
            
            sender = new Sender(host, port);

            sender.sendBytes(audioData);
            sender.sendBytes(audioData);
            
//            receiver.keepListening = false;
            
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            receiver.thread.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // AudioRecorder recorder = new AudioRecorder();
        // recorder.start(host, port);
        //
        // try {
        // // Wait for threads to stop
        // //receiver.thread.join();
        // recorder.thread.join();
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        System.exit(0);
    }

    private static String usage() {
        String usage = "Usage: <loss %> <latency in ms> <tcp/udp> <speech detection on/off>\n";
        usage += "   ex:  Main 0 40 tcp on\n";
        usage += "   ex:  Main 20 1000 udp off\n";

        return usage;
    }

}
