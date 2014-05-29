package speech_over_ip;

/**
 * A class I use as an extra Main() to test what I'm working on. Can be called,
 * but not usefully.
 * 
 * @author adveres
 * 
 */
public class Example {

    public static void main(String[] args) {
        
        String fileName = Constants.FILENAME_SOUND_RAW;
        
        byte[] audio = FileSaver.fileToBytes(fileName);
        AudioBytePlayer player = new AudioBytePlayer();
        player.Play(audio);
        System.exit(0);

        // Play that sound!
//        AudioBytePlayer bytePlayer = new AudioBytePlayer(FileSaver.fileToBytes(fileName));
//        bytePlayer.start();
//        try {
//            bytePlayer.thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
