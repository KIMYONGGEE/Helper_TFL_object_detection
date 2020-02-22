package org.tensorflow.lite.examples.detection.model;

public class USER {

    int _ID;
    String USERID;
    String PASS;
    String NAME;
    int AGE;
    String GENDER;
    String EMAIL;
    String PHONENUM;
    String ADDRESS;

    public USER(int _ID, String USERID, String PASS, String NAME, int AGE, String GENDER, String EMAIL, String PHONENUM, String ADDRESS) {
        this._ID = _ID;
        this.USERID = USERID;
        this.PASS = PASS;
        this.NAME = NAME;
        this.AGE = AGE;
        this.GENDER = GENDER;
        this.EMAIL = EMAIL;
        this.PHONENUM = PHONENUM;
        this.ADDRESS = ADDRESS;
    }

    public String getPASS() {
        return PASS;
    }

    public void setPASS(String PASS) {
        this.PASS = PASS;
    }

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public int getAGE() {
        return AGE;
    }

    public void setAGE(int AGE) {
        this.AGE = AGE;
    }

    public String getGENDER() {
        return GENDER;
    }

    public void setGENDER(String GENDER) {
        this.GENDER = GENDER;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getPHONENUM() {
        return PHONENUM;
    }

    public void setPHONENUM(String PHONENUM) {
        this.PHONENUM = PHONENUM;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }
}
