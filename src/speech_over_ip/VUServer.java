package speech_over_ip;

import java.io.*;
import java.net.*;

import javax.sound.sampled.*;

public class VUServer {

    ByteArrayOutputStream byteOutputStream;
    AudioFormat adFormat;
    TargetDataLine targetDataLine;
    AudioInputStream InputStream;
    SourceDataLine sourceLine;

    public static void main(String args[]) {
        new VUServer().runVOIP();
    }

    public void runVOIP() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(9876);
            byte[] receiveData = new byte[10000];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                System.out.println("RECEIVED: ");// + sentence);

                try {
                    byte audioData[] = receivePacket.getData();

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

    class PlayThread extends Thread {
        byte tempBuffer[] = new byte[10000];

        public void run() {
            try {
                int cnt;
                while ((cnt = InputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                    if (cnt > 0) {
                        sourceLine.write(tempBuffer, 0, cnt);
                    }
                }
                sourceLine.drain();
                sourceLine.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}
