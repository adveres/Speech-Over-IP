package data;

/**
 * A class to hold the statistics we uncover from the first 100ms of audio
 * 
 * @author adveres
 * 
 */
public class SpeechDetectionConfig {
    private double ITL = 0.0;
    private double ITU = 0.0;
    private double IZCT = 0.0;

    public SpeechDetectionConfig(double ITL, double ITU, double IZCT) {
        this.setITL(ITL);
        this.setITU(ITU);
        this.setIZCT(IZCT);
    }

    public String toString() {
        String s = "ITL: " + getITL() + ", ITU: " + getITU() + ", IZCT: " + getIZCT();
        return s;
    }

    public double getITL() {
        return ITL;
    }

    public void setITL(double iTL) {
        this.ITL = iTL;
    }

    public double getITU() {
        return ITU;
    }

    public void setITU(double iTU) {
        this.ITU = iTU;
    }

    public double getIZCT() {
        return IZCT;
    }

    public void setIZCT(double iZCT) {
        this.IZCT = iZCT;
    }
}
