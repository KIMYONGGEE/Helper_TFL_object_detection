package org.tensorflow.lite.examples.detection.model;

public class BACKGROUND {
    int BGID;
    String B_NAME;
    String B_ACCURACY;

    public BACKGROUND(int BGID, String b_NAME, String b_ACCURACY) {
        this.BGID = BGID;
        B_NAME = b_NAME;
        B_ACCURACY = b_ACCURACY;
    }

    public String getB_NAME() {
        return B_NAME;
    }

    public void setB_NAME(String b_NAME) {
        B_NAME = b_NAME;
    }

    public int getBGID() {
        return BGID;
    }

    public void setBGID(int BGID) {
        this.BGID = BGID;
    }

    public String getB_ACCURACY() {
        return B_ACCURACY;
    }

    public void setB_ACCURACY(String b_ACCURACY) {
        B_ACCURACY = b_ACCURACY;
    }
}
