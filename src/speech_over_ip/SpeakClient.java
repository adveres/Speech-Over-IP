package speech_over_ip;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.sound.sampled.*;

/**
 * A client that controls the sending of voice over the network
 * 
 * @author Adam Veres <adveres>
 * 
 */
public class SpeakClient extends JFrame {
    private static final long serialVersionUID = 5170909818194973861L;

    private AudioFormat format = Utils.getFormat();
    private boolean keepRecording = true;
    private TargetDataLine targetDataLine;

    private Configuration config;
    private Sender sender;
    
    private static final int WIDTH = 200;
    private static final int HEIGHT = 100;
    

    public SpeakClient(Configuration config) {
        this.config = config;
        this.sender = new Sender(config);

        final JButton start = new JButton("Start sending audio");
        final JButton stop = new JButton("Stop sending audio");

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
        setSize(WIDTH, HEIGHT);
        getContentPane().setBackground(Color.GRAY);
        setVisible(true);
    }

    private void captureAudio() {
        try {
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(format);
            targetDataLine.start();

            AudioCapture captureThread = new AudioCapture();
            captureThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Thread that loops, reading in audio from mic and sending over the
     * network.
     * 
     * @author Adam Veres <adveres>
     * 
     */
    class AudioCapture extends Thread {
        byte audioBuffer[] = new byte[Utils.latencyToBytes(config.getLatencyInMS())];

        public void run() {
            keepRecording = true;
            try {
                while (keepRecording) {
                    int count = targetDataLine.read(audioBuffer, 0, audioBuffer.length);
                    if (count > 0) {
                        sender.sendBytes(audioBuffer);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

}