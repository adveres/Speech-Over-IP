package speech_over_ip;


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
        System.out.print("Speak args: [");
        for (String s : args) {
            System.out.print(s + " ");
        }
        System.out.println("]\n");
        parseArguments(args);

        System.out.println(config);
        new SpeakServer(config).listen();
        new SpeakClient(config);
    }

    private static void printUsage() {
        String usage = "Usage: <host> <port> <loss %> <latency in ms> <tcp/udp> <detect speech true/false>";
        usage += " [ITL ITU]\n";
        usage += "   Note that ITL/ITU are optional.\n";
        usage += "   ex:  Main localhost 8080 0 40 tcp true\n";
        usage += "   ex:  Main 192.168.1.1 6222 20 1000 udp false\n";
        usage += "   ex:  Main 8.8.8.8 8000 0 20 udp true 200 400\n";

        System.out.println(usage);
    }

    /**
     * Parse args for acceptability.
     * 
     * @param args
     */
    private static void parseArguments(String[] args) {
        if (args.length < 6) {
            printUsage();
            System.exit(1);
        }

        config.setHost(args[0]);
        config.setPort(Integer.parseInt(args[1]));
        checkLossPct(Integer.parseInt(args[2]));
        checkLatency(Integer.parseInt(args[3]));
        checkPacketType(args[4]);
        checkDetectSpeech(args[5]);

        if (args.length == 7) {
            check_ITL(Integer.parseInt(args[6]));
        } else if (args.length == 8) {
            check_ITL_ITU(Integer.parseInt(args[6]), Integer.parseInt(args[7]));
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
     * Sanity on latency integer (in ms). 20-1000ms supported.
     * 
     * @param lat input to check
     */
    private static void checkLatency(int lat) {
        if (lat < Constants.MIN_LATENCY) {
            System.err.println("Latency cannot be below " + Constants.MIN_LATENCY);
            printUsage();
            System.exit(1);
        }
        if (lat > Constants.CHUNK_OF_1000MS) {
            System.err.println("Latency above 1000ms is unsupported.");
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
            System.err.println("Speech Detection must be 'true' or 'false'");
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
