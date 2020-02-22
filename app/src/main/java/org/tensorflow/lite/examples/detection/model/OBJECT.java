package org.tensorflow.lite.examples.detection.model;

public class OBJECT {

    int OBJECTID;
    String OBJECTNAME;
    int PRIORITY;
    String O_ACCURACY;

    public OBJECT() {
    }

    public OBJECT(int OBJECTID, String OBJECTNAME, int PRIORITY, String o_ACCURACY) {
        this.OBJECTID = OBJECTID;
        this.OBJECTNAME = OBJECTNAME;
        this.PRIORITY = PRIORITY;
        O_ACCURACY = o_ACCURACY;
    }

    public int getOBJECTID() {
        return OBJECTID;
    }

    public void setOBJECTID(int OBJECTID) {
        this.OBJECTID = OBJECTID;
    }

    public String getOBJECTNAME() {
        return OBJECTNAME;
    }

    public void setOBJECTNAME(String OBJECTNAME) {
        this.OBJECTNAME = OBJECTNAME;
    }

    public int getPRIORITY() {
        return PRIORITY;
    }

    public void setPRIORITY(int PRIORITY) {
        this.PRIORITY = PRIORITY;
    }

    public String getO_ACCURACY() {
        return O_ACCURACY;
    }

    public void setO_ACCURACY(String o_ACCURACY) {
        O_ACCURACY = o_ACCURACY;
    }
}
