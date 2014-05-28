package speech_over_ip;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.sound.sampled.*;

public class VUClient extends JFrame {
    private static final long serialVersionUID = 351986955428645064L;

    boolean captureAudio = false;
    ByteArrayOutputStream byteOutputStream;
    AudioFormat adFormat;
    TargetDataLine targetDataLine;
    AudioInputStream InputStream;
    SourceDataLine sourceLine;
    Graphics g;

    private static final int HEIGHT = 200;
    private static final int WIDTH = 200;

    private static final String HOST = "localhost";
    private static final int PORT = 6222;


    public static void main(String args[]) {
        new VUClient();
    }

    public VUClient() {
        final JButton capture = new JButton("Start");
        final JButton stop = new JButton("Stop");

        capture.setEnabled(true);
        stop.setEnabled(false);

        capture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                capture.setEnabled(false);
                stop.setEnabled(true);
                captureAudio();
            }
        });
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                capture.setEnabled(true);
                stop.setEnabled(false);
                captureAudio = true;
                targetDataLine.close();
            }
        });

        getContentPane().add(capture);
        getContentPane().add(stop);

        addSamplingSlider(getContentPane());
        addRadioButtons(getContentPane());

        getContentPane().setLayout(new FlowLayout());
        setTitle("Speak");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        getContentPane().setBackground(Color.white);
        setVisible(true);

        g = (Graphics) this.getGraphics();
    }

    private void addRadioButtons(Container contentPane) {
        JRadioButton tcpButton = new JRadioButton("TCP");
        tcpButton.setMnemonic(KeyEvent.VK_T);
        tcpButton.setActionCommand("TCP");

        JRadioButton udpButton = new JRadioButton("UDP");
        udpButton.setMnemonic(KeyEvent.VK_U);
        udpButton.setActionCommand("UDP");

        ButtonGroup group = new ButtonGroup();
        group.add(tcpButton);
        group.add(udpButton);

        contentPane.add(tcpButton);
        contentPane.add(udpButton);
    }
    
    private void addSpeechButtons(Container contentPane) {
        JRadioButton tcpButton = new JRadioButton("On");
        tcpButton.setMnemonic(KeyEvent.VK_N);
        tcpButton.setActionCommand("ON");

        JRadioButton udpButton = new JRadioButton("UDP");
        udpButton.setMnemonic(KeyEvent.VK_U);
        udpButton.setActionCommand("OFF");

        ButtonGroup group = new ButtonGroup();
        group.add(tcpButton);
        group.add(udpButton);

        contentPane.add(tcpButton);
        contentPane.add(udpButton);
    }

    private void addSamplingSlider(Container contentPane) {
        JSlider sampling = new JSlider(JSlider.HORIZONTAL);
        sampling.setEnabled(true);

        // Turn on labels at major tick marks.
        sampling.setMajorTickSpacing(20);
        sampling.setMinorTickSpacing(0);
        sampling.setPaintTicks(true);
        sampling.setPaintLabels(true);
        sampling.setSnapToTicks(true);
        JLabel sliderLabel = new JLabel("Samples/sec", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPane.add(sliderLabel);
        contentPane.add(sampling);
    }

    private void captureAudio() {
        try {
            adFormat = Utils.getFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, adFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(adFormat);
            targetDataLine.start();

            Thread captureThread = new Thread(new CaptureThread());
            captureThread.start();
        } catch (Exception e) {
            StackTraceElement stackEle[] = e.getStackTrace();
            for (StackTraceElement val : stackEle) {
                System.out.println(val);
            }
            System.exit(0);
        }
    }

    class CaptureThread extends Thread {

        byte tempBuffer[] = new byte[10000];

        public void run() {

            byteOutputStream = new ByteArrayOutputStream();
            captureAudio = true;
            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName(HOST);

                while (captureAudio) {
                    int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    if (cnt > 0) {
                        DatagramPacket sendPacket = new DatagramPacket(tempBuffer,
                                tempBuffer.length, IPAddress, PORT);
                        clientSocket.send(sendPacket);

                        byteOutputStream.write(tempBuffer, 0, cnt);
                    }
                }

                byteOutputStream.close();

            } catch (Exception e) {
                System.out.println("CaptureThread::run()" + e);
                System.exit(0);
            }
        }
    }
}
