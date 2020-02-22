package org.tensorflow.lite.examples.detection.model;

public class VOICEDATA {

    int VOICEID;
    int PAGENUM;
    String OUTPUTVOICE;

    public VOICEDATA(int VOICEID, int PAGENUM, String OUTPUTVOICE) {
        this.VOICEID = VOICEID;
        this.PAGENUM = PAGENUM;
        this.OUTPUTVOICE = OUTPUTVOICE;
    }

    public int getVOICEID() {
        return VOICEID;
    }

    public void setVOICEID(int VOICEID) {
        this.VOICEID = VOICEID;
    }

    public int getPAGENUM() {
        return PAGENUM;
    }

    public void setPAGENUM(int PAGENUM) {
        this.PAGENUM = PAGENUM;
    }

    public String getOUTPUTVOICE() {
        return OUTPUTVOICE;
    }

    public void setOUTPUTVOICE(String OUTPUTVOICE) {
        this.OUTPUTVOICE = OUTPUTVOICE;
    }
}
