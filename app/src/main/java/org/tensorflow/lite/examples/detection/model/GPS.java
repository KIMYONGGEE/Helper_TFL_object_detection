package org.tensorflow.lite.examples.detection.model;

public class GPS {

    int GPSID;
    String DATE;
    String PATHNAME;

    String StartLATITUDE;
    String StartLONGITUDE;

    String EndLATITUDE;
    String EndLONGITUDE;

    public GPS(int GPSID, String DATE, String PATHNAME, String startLATITUDE, String startLONGITUDE, String endLATITUDE, String endLONGITUDE) {
        this.GPSID = GPSID;
        this.DATE = DATE;
        this.PATHNAME = PATHNAME;
        StartLATITUDE = startLATITUDE;
        StartLONGITUDE = startLONGITUDE;
        EndLATITUDE = endLATITUDE;
        EndLONGITUDE = endLONGITUDE;
    }

    public int getGPSID() {
        return GPSID;
    }

    public void setGPSID(int GPSID) {
        this.GPSID = GPSID;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getPATHNAME() {
        return PATHNAME;
    }

    public void setPATHNAME(String PATHNAME) {
        this.PATHNAME = PATHNAME;
    }

    public String getStartLATITUDE() {
        return StartLATITUDE;
    }

    public void setStartLATITUDE(String startLATITUDE) {
        StartLATITUDE = startLATITUDE;
    }

    public String getStartLONGITUDE() {
        return StartLONGITUDE;
    }

    public void setStartLONGITUDE(String startLONGITUDE) {
        StartLONGITUDE = startLONGITUDE;
    }

    public String getEndLATITUDE() {
        return EndLATITUDE;
    }

    public void setEndLATITUDE(String endLATITUDE) {
        EndLATITUDE = endLATITUDE;
    }

    public String getEndLONGITUDE() {
        return EndLONGITUDE;
    }

    public void setEndLONGITUDE(String endLONGITUDE) {
        EndLONGITUDE = endLONGITUDE;
    }
}
