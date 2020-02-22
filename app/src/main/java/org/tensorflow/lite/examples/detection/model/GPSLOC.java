package org.tensorflow.lite.examples.detection.model;

public class GPSLOC {

    int GPSID;
    String LATITUDE;
    String LONGITUDE;

    public GPSLOC(int GPSID, String LATITUDE, String LONGITUDE) {
        this.GPSID = GPSID;
        this.LATITUDE = LATITUDE;
        this.LONGITUDE = LONGITUDE;
    }

    public int getGPSID() {
        return GPSID;
    }

    public void setGPSID(int GPSID) {
        this.GPSID = GPSID;
    }

    public String getLATITUDE() {
        return LATITUDE;
    }

    public void setLATITUDE(String LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public String getLONGITUDE() {
        return LONGITUDE;
    }

    public void setLONGITUDE(String LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }
}
