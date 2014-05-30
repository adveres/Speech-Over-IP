package speech_over_ip;

import data.Configuration;
import utilities.Constants;
import utilities.Utils;

/**
 * Loss: 0%, 1%, 5%, 10%, 20%
 * 
 * Latency (by increasing the sample interval): 40ms,100ms, 250ms, 500ms, 1000ms
 * 
 * Connection type: TCP, UDP
 * 
 * Speech detection: on,off
 * 
 * @author Adam Veres <adveres>
 * 
 */
public class Main {

    private static Configuration config = new Configuration();

    public static void main(String[] args) {
        parseArguments(args);

        // config.setHost("192.168.1.10");
        config.setHost("192.168.1.11");
        // config.setHost("localhost");
        // config.setHost("127.0.0.1");
        config.setPort(6222);

        if (0 == config.getPacketType().compareTo(Constants.UDP)) {
            new SpeakClient(config);
            new SpeakServer(config).listen();
        } else {
            System.err.println("--------------------");
            System.err.println("     TCP IS TODO");
            System.err.println("--------------------");
        }
    }

    private static void printUsage() {
        String usage = "Usage: <loss %> <latency in ms> <tcp/udp> <detect speech true/false>";
        usage += "[ITL ITU]\n";
        usage += "   ex:  Main 0 40 tcp true\n";
        usage += "   ex:  Main 20 1000 udp false\n";
        usage += "   ex:  Main 0 20 udp true 200 400\n";

        System.out.println(usage);
    }

    /**
     * Parse args for acceptability.
     * 
     * @param args
     */
    private static void parseArguments(String[] args) {
        if (args.length < 4) {
            printUsage();
            System.exit(1);
        }

        checkLossPct(Integer.parseInt(args[0]));
        checkLatency(Integer.parseInt(args[1]));
        checkPacketType(args[2]);
        checkDetectSpeech(args[3]);

        if (args.length == 5) {
            check_ITL(Integer.parseInt(args[4]));
        } else if (args.length == 6) {
            check_ITL_ITU(Integer.parseInt(args[4]), Integer.parseInt(args[5]));
        }
    }

    /**
     * Sanity on packet loss percentage
     * 
     * @param lossArg
     */
    private static void checkLossPct(int lossArg) {
        int loss = -1;
        for (int i = 0; i < Constants.LOSS_PERCENTAGES.length; i++) {
            if (lossArg == Constants.LOSS_PERCENTAGES[i]) {
                loss = Constants.LOSS_PERCENTAGES[i];
                break;
            }
        }

        if (loss == -1) {
            System.err.println("Acceptable loss percentages are: "
                    + Utils.arrayToString(Constants.LOSS_PERCENTAGES));
            printUsage();
            System.exit(1);
        } else {
            config.setLossPercent(loss);
        }
    }

    /**
     * Sanity on latency integer (in ms)
     * 
     * @param lat
     */
    private static void checkLatency(int lat) {
        if (lat < Constants.MIN_LATENCY) {
            System.err.println("Latency cannot be below " + Constants.MIN_LATENCY);
            printUsage();
            System.exit(1);
        }
        config.setLatencyInMS(lat);
    }

    /**
     * Check packet type is TCP or UDP
     * 
     * @param pktType
     */
    private static void checkPacketType(String pktType) {
        String pack = pktType.toUpperCase();

        if (0 == pack.compareTo(Constants.UDP)) {
            config.setPacketType(Constants.UDP);
        } else if (0 == pack.compareTo(Constants.TCP)) {
            config.setPacketType(Constants.TCP);
        } else {
            System.err.println("Packet type must be one of [" + Constants.TCP + ", "
                    + Constants.UDP + "]");
            printUsage();
            System.exit(1);
        }
    }

    /**
     * Check detectSpeech param is 'true' or 'false'
     * 
     * @param detSpeech
     */
    private static void checkDetectSpeech(String detSpeech) {
        String det = detSpeech.toUpperCase();

        if (0 == det.compareTo("TRUE")) {
            config.setSpeechDetectionOn(true);
        } else if (0 == det.compareTo("FALSE")) {
            config.setSpeechDetectionOn(false);
        } else {
            System.err.println("Packet type must be 'true' or 'false'");
            printUsage();
            System.exit(1);
        }
    }

    private static void check_ITL(double itl) {
        check_ITL_ITU(itl, config.getSpeechConfig().getITU());
    }

    private static void check_ITL_ITU(double itl, double itu) {
        if (itl < 0 || itu < 0) {
            System.err
                    .println("Neither ITL not ITU should be negative. Recommend values between 0-1000.");
            printUsage();
            System.exit(1);
        }
        config.getSpeechConfig().setITL(itl);
        config.getSpeechConfig().setITU(itu);
    }

}
