package speech_over_ip;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * A class that runs as a thread to play sound.
 * 
 * @author adveres
 * 
 */
public class AudioBytePlayer extends Thread {
    SourceDataLine line;

    private AudioFormat audioFormat = null;
    private SourceDataLine source = null;

    public AudioBytePlayer() {
        init();
    }

    /**
     * Set up the audio stuff to play sound later!
     */
    public void init() {
        // Grab the audio format ONCE so we don't keep init'ing it every time
        // playBytes is called.
        this.audioFormat = Utils.getFormat();

        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, Utils.getFormat());
        try {
            // get the output and initialize it with the desired audio format
            this.source = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            this.source.open(this.audioFormat);
        } catch (LineUnavailableException e) {
            System.err.println("Unable to open the line: " + e);
            e.printStackTrace();
            return;
        }

        // open the speakers for play back
        this.source.start();
    }

    /**
     * Given a byte array of audio data, play it to speakers!
     * 
     * @param data
     */
    public void playBytes(byte[] data) {
        byte buff[] = new byte[data.length];

        // now that the source file is open, we can read it
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        long streamLength = data.length / this.audioFormat.getFrameSize();
        AudioInputStream audioInputStream = new AudioInputStream(inputStream, this.audioFormat,
                streamLength);
        try {
            int bytesRead = 0;
            // Returns -1 at EOF
            while (-1 != bytesRead) {
                bytesRead = audioInputStream.read(buff, 0, buff.length);
                if (bytesRead > 0) {
                    this.source.write(buff, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}