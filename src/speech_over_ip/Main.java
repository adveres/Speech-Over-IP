package speech_over_ip;

import java.io.IOException;
import java.net.UnknownHostException;

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

    private static String _host;
    private static int _port;

    private static int _loss;
    private static int _latency;
    private static String _packetType;
    private static boolean _detectSpeech;

    private static int _ITL = 200;
    private static int _ITU = 400;

    public static void main(String[] args) {
        parseArguments(args);

        // host = "192.168.1.10";
        _host = "192.168.1.11";
        // host = "localhost";
        // host = "127.0.0.1";
        _port = 6222;

        if (0 == _packetType.compareTo(Constants.UDP)) {
            SpeakClient speak = new SpeakClient(_host, _port, _latency);
            new SpeakServer(_port, _latency, _loss).listen();
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
        _loss = -1;
        for (int i = 0; i < Constants.LOSS_PERCENTAGES.length; i++) {
            if (lossArg == Constants.LOSS_PERCENTAGES[i]) {
                _loss = Constants.LOSS_PERCENTAGES[i];
                break;
            }
        }

        if (_loss == -1) {
            System.err.println("Acceptable loss percentages are: "
                    + Utils.arrayToString(Constants.LOSS_PERCENTAGES));
            printUsage();
            System.exit(1);
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
        _latency = lat;
    }

    /**
     * Check packet type is TCP or UDP
     * 
     * @param pktType
     */
    private static void checkPacketType(String pktType) {
        String pack = pktType.toUpperCase();

        if (0 == pack.compareTo(Constants.UDP)) {
            _packetType = Constants.UDP;
        } else if (0 == pack.compareTo(Constants.TCP)) {
            _packetType = Constants.TCP;
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
            _detectSpeech = true;
        } else if (0 == det.compareTo("FALSE")) {
            _detectSpeech = false;
        } else {
            System.err.println("Packet type must be 'true' or 'false'");
            printUsage();
            System.exit(1);
        }
    }

    private static void check_ITL(int itl) {
        check_ITL_ITU(itl, _ITU);
    }

    private static void check_ITL_ITU(int itl, int itu) {
        if (itl < 0 || itu < 0) {
            System.err
                    .println("Neither ITL not ITU should be negative. Recommend values between 0-1000.");
            printUsage();
            System.exit(1);
        }
        _ITL = itl;
        _ITU = itu;
    }

}
