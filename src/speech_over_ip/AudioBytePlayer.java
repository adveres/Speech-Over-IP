package speech_over_ip;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import utilities.Constants;
import utilities.OSUtils;
import utilities.Utils;

/**
 * A class that runs as a thread to play sound.
 * 
 * @author adveres
 * 
 */
public class AudioBytePlayer extends Thread {
    SourceDataLine line;
    Thread thread;
    byte[] audioBytes;

    public AudioBytePlayer() {
        this.audioBytes = null;
    }

    public AudioBytePlayer(byte[] audioData) {
        this.audioBytes = audioData;
    }

    public void run() {

        if (audioBytes == null) {
            System.err.println("No audio bytes to play");
            return;
        }

        AudioFormat format = Utils.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Line matching " + info + " not supported.");
            return;
        }

        // get and open the source data line for playback.

        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, audioBytes.length);
        } catch (LineUnavailableException ex) {
            System.err.println("Unable to open the line: " + ex);
            return;
        }

        line.start();
        line.write(audioBytes, 0, audioBytes.length);
        line.drain();
        line.stop();
        line.close();
        line = null;
    }

    public void Play(byte[] audioData) {
        this.audioBytes = audioData;
        this.start();
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}