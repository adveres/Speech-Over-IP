package utilities;


public class Constants {
    public static final String FILENAME_SOUND_DATA = "sound.data";
    public static final String FILENAME_SOUND_RAW = "sound.raw";
    public static final String FILENAME_SPEECH_RAW = "speech.raw";
    public static final String FILENAME_ENERGY_DATA = "energy.data";
    public static final String FILENAME_ZERO_DATA = "zero.data";

    public static final int CHUNK_OF_10MS = (int) (10 * (Utils.getFormat().getFrameRate() / 1000));
    public static final int CHUNK_OF_40MS = 4 * CHUNK_OF_10MS;
    public static final int CHUNK_OF_100MS = 10 * CHUNK_OF_10MS;
    public static final int CHUNK_OF_200MS = 20 * CHUNK_OF_10MS;
    public static final int CHUNK_OF_250MS = 25 * CHUNK_OF_10MS;
    public static final int CHUNK_OF_500MS = 50 * CHUNK_OF_10MS;
    public static final int CHUNK_OF_1000MS = 100 * CHUNK_OF_10MS;

    public static final int REQUIRED_ZERO_CROSSINGS = 3;

    public static final int MIN_LATENCY = 20;

    public static final int[] LOSS_PERCENTAGES = { 0, 1, 5, 10, 20 };
    public static final String[] LOSS_PERCENTAGES_STR = { "0", "1", "5", "10", "20" };
    
    public static final String TCP = "TCP";
    public static final String UDP = "UDP";
}
