package data;

import utilities.Constants;

/**
 * Configuration options for the program. Contains really everything the
 * client/server/algorithm needs to run based on input parameters.
 * 
 * @author Adam Veres <adveres>
 * 
 */
public class Configuration {
    private String host = "localhost";
    private int port = 6222;
    private int lossPercent = 0;
    private int latencyInMS = 1000;
    private boolean speechDetectionOn = true;
    private String packetType = Constants.UDP;

    private SpeechDetectionConfig speechConfig = new SpeechDetectionConfig(200.0, 400.0, 0.0);

    /**
     * Default constructor is OK. Have defaults set above.
     */
    public Configuration() {

    }

    /**
     * Alternatively callers may specify each and every option.
     * 
     * @param host String of the host
     * @param port port number to talk over
     * @param loss percentage
     * @param latency in milliseconds
     * @param speechDetectionOn whether or not to filter silence
     * @param packetType UDP or TCP
     */
    public Configuration(String host, int port, int loss, int latency, boolean speechDetectionOn,
            String packetType) {
        this.setHost(host);
        this.setPort(port);
        this.setLossPercent(loss);
        this.setLatencyInMS(latency);
        this.setSpeechDetectionOn(speechDetectionOn);
        this.setPacketType(packetType);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getLossPercent() {
        return lossPercent;
    }

    public void setLossPercent(int loss) {
        this.lossPercent = loss;
    }

    public int getLatencyInMS() {
        return latencyInMS;
    }

    public void setLatencyInMS(int latency) {
        this.latencyInMS = latency;
    }

    public boolean isSpeechDetectionOn() {
        return speechDetectionOn;
    }

    public void setSpeechDetectionOn(boolean speechDetectionOn) {
        this.speechDetectionOn = speechDetectionOn;
    }

    public String getPacketType() {
        return packetType;
    }

    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }

    public boolean isPacketTypeUDP() {
        return (0 == this.getPacketType().compareTo(Constants.UDP));
    }

    public boolean isPacketTypeTCP() {
        return (0 == this.getPacketType().compareTo(Constants.TCP));
    }

    public SpeechDetectionConfig getSpeechConfig() {
        return speechConfig;
    }

    public void setSpeechConfig(SpeechDetectionConfig speechConfig) {
        this.speechConfig = speechConfig;
    }

}
