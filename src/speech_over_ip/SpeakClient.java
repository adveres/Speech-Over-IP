package speech_over_ip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class SpeakClient extends JFrame {
    private static final long serialVersionUID = 5170909818194973861L;

    AudioFormat format = Utils.getFormat();
    boolean keepRecording = true;
    TargetDataLine targetDataLine;
    AudioInputStream InputStream;
    SourceDataLine sourceLine;

    private String HOST = "localhost";
    private int PORT = 0;

    public SpeakClient(String host, int port) {
        HOST = host;
        PORT = port;

        final JButton start = new JButton("Start");
        final JButton stop = new JButton("Stop");

        start.setEnabled(true);
        stop.setEnabled(false);

        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                start.setEnabled(false);
                stop.setEnabled(true);
                captureAudio();
            }
        });

        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                start.setEnabled(true);
                stop.setEnabled(false);
                keepRecording = false;
                targetDataLine.close();
            }
        });
        getContentPane().add(start);
        getContentPane().add(stop);

        getContentPane().setLayout(new FlowLayout());
        setTitle("Speak");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(200, 75);
        getContentPane().setBackground(Color.white);
        setVisible(true);
    }

    private void captureAudio() {
        try {
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(format);
            targetDataLine.start();

            Thread captureThread = new Thread(new AudioCapture());
            captureThread.start();
        } catch (Exception e) {
            StackTraceElement stackEle[] = e.getStackTrace();
            for (StackTraceElement val : stackEle) {
                System.out.println(val);
            }
            System.exit(0);
        }
    }

    class AudioCapture extends Thread {

        byte audioBuffer[] = new byte[Constants.CHUNK_OF_250MS];

        public void run() {
            keepRecording = true;
            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName(HOST);

                while (keepRecording) {
                    int count = targetDataLine.read(audioBuffer, 0, audioBuffer.length);
                    if (count > 0) {
                        DatagramPacket sendPacket = new DatagramPacket(audioBuffer,
                                audioBuffer.length, IPAddress, PORT);
                        clientSocket.send(sendPacket);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

}// Class End 