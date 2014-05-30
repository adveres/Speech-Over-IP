package utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import data.SpeechDetectionConfig;

/**
 * Class with common methods for the package.
 * 
 * @author adveres
 * 
 */

public class Utils {
    public static final float RATE = 8000.0f; // Given by proj1 definition
    public static final int SAMPLE_SIZE = 8; // Given by proj1 definition
    public static final int CHANNELS = 1; // Mono, given by proj1 definition

    /**
     * Returns an audio format.
     * 
     * @return AudioFormat instance with specified values
     */
    public static AudioFormat getFormat() {
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        boolean bigEndian = false;
        int frameSize = ((SAMPLE_SIZE / 8) * CHANNELS); // 8/8*1 = 1 byte

        // 8000 samples/sec, 8 bits per sample, 1 channel=mono,
        return new AudioFormat(encoding, RATE, SAMPLE_SIZE, CHANNELS, frameSize, RATE, bigEndian);
    }

    /**
     * Takes a latency in MS and returns the bytes to read in to get that
     * millisecond amount.
     * 
     * @param latencyInMS
     * @return bytes equaling latencyInMS milliseconds
     */
    public static int latencyToBytes(int latencyInMS) {
        return (int) (latencyInMS * (Utils.getFormat().getFrameRate() / 1000));
    }

    /**
     * Convert an AudioInputStream to an array of integers for processing
     * 
     * @param ais an AudioInputStream of audio data
     * @return array of ints
     */
    public static int[] findIntegerDataFromAudio(AudioInputStream ais) {
        AudioFormat format = ais.getFormat();
        byte[] audioBytes = new byte[(int) (ais.getFrameLength() * format.getFrameSize())];

        // calculate durations
        long durationMSec = (long) ((ais.getFrameLength() * 1000) / ais.getFormat().getFrameRate());
        double durationSec = durationMSec / 1000.0;
        System.out.println("The current signal has duration " + durationSec + " Sec");

        try {
            ais.read(audioBytes);
        } catch (IOException e) {
            System.out.println("IOException during reading audioBytes");
            e.printStackTrace();
        }

        return byte_array_to_ints(format, audioBytes);
    }

    /**
     * Converts an array of bytes to array of ints for "raw data". The format is
     * expected to be the same format we record in.
     * 
     * @param format the extracted AudioFormat from AudioInputStream above
     * @param audioBytes array of bytes of audio data
     * @return data an int array
     */
    public static int[] byte_array_to_ints(AudioFormat format, byte[] audioBytes) {
        int[] data = null;

        if (format.getSampleSizeInBits() == Utils.SAMPLE_SIZE) {
            int nlengthInSamples = audioBytes.length;
            data = new int[nlengthInSamples];
            for (int i = 0; i < audioBytes.length; i++) {
                data[i] = audioBytes[i];
            }
        } else {
            System.err.println("Unsupported audio format specified. Unable to process.");
        }

        return data;
    }

    public static int[] byte_array_to_ints(byte[] audioBytes) {
        return byte_array_to_ints(Utils.getFormat(), audioBytes);
    }

    public static int[] calculateZeroCrossingsInChunks(int[] wholeArray, int chunkSize) {
        int[] zeroCrossings = new int[wholeArray.length / chunkSize];

        for (int x = 0; x < zeroCrossings.length; x++) {
            int offset = x * chunkSize;
            int[] chunk = Arrays.copyOfRange(wholeArray, offset, (offset + chunkSize));

            int zeroCt = Utils.calculateZeroCrossings(chunk);
            zeroCrossings[x] = zeroCt;
        }

        return zeroCrossings;
    }

    /**
     * Calculate zero crossings in an integer array
     * 
     * @param data array of integers
     * @return count of number of times we cross zero
     */
    public static int calculateZeroCrossings(int[] data) {
        if (null == data || data.length == 0) {
            System.err.println("Bad data given.");
            return -1;
        }

        int zeroCrossings = 0;

        // Find the first nonzero.
        int last_nonzero = data[0];
        int last_nonzero_index = 0;
        for (int x = 0; x < data.length; x++) {
            if (data[x] != 0) {
                last_nonzero = data[x];
                last_nonzero_index = x;
                break;
            }
            if (x == data.length - 1) {
                return 0;
            }
        }

        // Find other nonzeros
        for (int x = last_nonzero_index; x < data.length; x++) {

            if (data[x] == 0) {
                continue;
            }
            boolean prev_x_neg = last_nonzero < 0 ? true : false;
            boolean curr_x_neg = data[x] < 0 ? true : false;
            if (prev_x_neg != curr_x_neg) {
                // System.out.println("last = (" + last_nonzero_index + ": " +
                // last_nonzero
                // + "). New=(" + x + ": " + data[x] + ")");
                zeroCrossings++;
                last_nonzero = data[x];
                last_nonzero_index = x;
            }
        }
        return zeroCrossings;
    }

    public static int energyOfChunk(int[] data) {
        return MathHelper.abs_sum(data);
    }

    public static int[] energyOfArray(int[] data, int chunkSize) {
        if (null == data || data.length == 0) {
            System.err.println("Invalid data array given.");
            return null;
        }

        int[] energy = new int[data.length / chunkSize];
        for (int x = 0; x < data.length / chunkSize; x++) {
            int offset = x * (chunkSize);
            int[] chunk = Arrays.copyOfRange(data, offset, (offset + chunkSize));
            int chunkEnergy = Utils.energyOfChunk(chunk);
            energy[x] = chunkEnergy;
        }

        return energy;
    }

    public static String arrayToString(int[] arr) {
        System.out.println(arr.length);
        String s = "[";
        for (int x = 0; x < arr.length; x++) {
            // System.out.println(x);
            s += arr[x];
            if (x != arr.length - 1) {
                s += ", ";
            }
        }
        s += "]";
        return s;
    }

    public static byte[] toByteArray(ArrayList<Byte> in) {
        int n = in.size();
        byte ret[] = new byte[n];
        for (int x = 0; x < n; x++) {
            ret[x] = in.get(x);
        }
        return ret;
    }

    /**
     * Runs the first 100ms algorithm on the first 800 bytes of raw sound
     * 
     * @param rawSoundBytes
     * @return
     */
    public static SpeechDetectionConfig analyzeFirst100ms(byte[] rawSoundBytes) {
        byte[] first_800_bytes = new byte[Constants.CHUNK_OF_100MS];
        for (int x = 0; x < Constants.CHUNK_OF_100MS; x++) {
            first_800_bytes[x] = rawSoundBytes[x];
        }

        return Algorithms.processFirst100ms(first_800_bytes);
    }

}