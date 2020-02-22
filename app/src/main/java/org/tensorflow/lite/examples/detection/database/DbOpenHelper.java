package org.tensorflow.lite.examples.detection.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tensorflow.lite.examples.detection.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class DbOpenHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Table Names
    private static final String TABLE_USER = "USER";
    private static final String TABLE_OBJECT = "OBJECT";
    private static final String TABLE_BACKGROUND = "BACKGROUND";
    private static final String TABLE_GPS = "GPS";
    private static final String TABLE_GPSLOC = "GPSLOC";
    private static final String TABLE_VOICEDATA = "VOICEDATA";

    /*UserTable*/
    public static final String _ID = "_id";
    public static final String USERID = "userid";
    public static final String PASS = "pass";
    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String GENDER = "gender";
    public static final String EMAIL = "email";
    public static final String PHONENUM = "phone";
    public static final String ADDRESS = "ADDRESS";

    /*Object Table*/
    public static final String OBJECTID = "objectid";
    public static final String OBJECTNAME = "objectname";
    public static final String PRIORITY = "priority";
    public static final String O_ACCURACY = "o_accuracy";

    /*BackGroundTable*/
    public static final String BGID = "bgid";
    public static final String B_ACCURACY = "b_accuracy";
    public static final String B_NAME = "b_name";

    /*GPS table*/
    public static final String GPSID = "gpsid";
    public static final String DATE = "date";
    public static final String PATHNAME = "pathname";

    public static final String STARTLATITUDE = "startlatitude";
    public static final String STARTLONGITUDE = "startlongtitude";
    public static final String ENDLATITUDE = "endlatitude";
    public static final String ENDLONGITUDE = "endlongtitude";

    /*GPS_LOC table*/
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longtitude";

    /*VOICE_DATA table*/
    public static final String VOICEID = "voiceid";
    public static final String PAGENUM = "pagenum";
    public static final String OUTPUTVOICE = "outputvoice";

    /*UserTable*/
    public static final String Create_Table_USER = "create table if not exists " + TABLE_USER + "("
            + _ID + " integer primary key autoincrement, "
            + USERID + " text not null , "
            + PASS + " text not null , "
            + NAME + " text not null , "
            + AGE + " integer not null , "
            + GENDER + " text not null , "
            + EMAIL + " text, "
            + PHONENUM + " text, "
            + ADDRESS + " text);";

    /*Object Table*/
    public static final String Create_Table_OBJECT = "create table if not exists " + TABLE_OBJECT + "("
            + OBJECTID + " integer primary key autoincrement , "
            + OBJECTNAME + " text not null , "
            + PRIORITY + " integer not null , "
            + O_ACCURACY + " text not null );";

    /*Back Ground Table */
    public static final String Create_Table_Background = "create table if not exists " + TABLE_BACKGROUND + "("
            + BGID + " integer primary key autoincrement , "
            + B_NAME + " text not null , "
            + B_ACCURACY + " text not null );";

    /*GPS table*/
    public static final String Create_Table_GPS = "create table if not exists " + TABLE_GPS + "("
            + GPSID + " integer primary key autoincrement , "
            + DATE + " text not null , "
            + PATHNAME + " text not null, "
            + STARTLATITUDE + " text , "
            + STARTLONGITUDE + " text , "
            + ENDLATITUDE + " text , "
            + ENDLONGITUDE+ " text );";

    /*GPS_LOC table*/
    public static final String Create_Table_GPSLOC = "create table if not exists " + TABLE_GPSLOC + "("
            + GPSID + " integer not null , "
            + LATITUDE + " text not null , "
            + LONGITUDE + " text not null );";

    /*VOICE_DATA table*/
    public static final String Create_Table_VoiceDATA = "create table if not exists " + TABLE_VOICEDATA + "("
            + VOICEID + " integer primary key autoincrement  , "
            + PAGENUM + " integer not null , "
            + OUTPUTVOICE + " text not null );";

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_Table_OBJECT);
        db.execSQL(Create_Table_USER);
        db.execSQL(Create_Table_Background);
        db.execSQL(Create_Table_GPS);
        db.execSQL(Create_Table_GPSLOC);
        db.execSQL(Create_Table_VoiceDATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBJECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BACKGROUND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPSLOC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOICEDATA);

        onCreate(db);
    }


    // 유저 테이블 insert
    public long createUSER(USER user){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(_ID, user.get_ID());
        values.put(USERID, user.getUSERID());
        values.put(PASS, user.getPASS());
        values.put(NAME, user.getNAME());
        values.put(AGE, user.getAGE());
        values.put(GENDER, user.getGENDER());
        values.put(EMAIL, user.getEMAIL());
        values.put(PHONENUM, user.getPHONENUM());
        values.put(ADDRESS, user.getADDRESS());

        return db.insert(TABLE_USER, null, values);
    }

    // 오브젝트 테이블 insert
    public long createOBJECT(OBJECT object){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(OBJECTID, object.getOBJECTID());
        values.put(OBJECTNAME, object.getOBJECTNAME());
        values.put(PRIORITY, object.getPRIORITY());
        values.put(O_ACCURACY, object.getO_ACCURACY());

        return db.insert(TABLE_OBJECT, null, values);
    }

    // 백그라운드 테이블 insert
    public long createBACKGROUND(BACKGROUND background){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BGID, background.getBGID());
        values.put(B_NAME, background.getB_NAME());
        values.put(B_ACCURACY, background.getB_ACCURACY());

        return db.insert(TABLE_BACKGROUND, null, values);
    }

    // GPS 테이블 insert
    public long createGPS(GPS inputgps){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GPSID, inputgps.getGPSID());
        values.put(DATE, inputgps.getDATE());
        values.put(PATHNAME, inputgps.getPATHNAME());
        values.put(STARTLATITUDE, inputgps.getStartLATITUDE());
        values.put(STARTLONGITUDE, inputgps.getStartLONGITUDE());
        values.put(ENDLATITUDE, inputgps.getEndLATITUDE());
        values.put(ENDLONGITUDE, inputgps.getEndLONGITUDE());

        return db.insert(TABLE_GPS, null, values);
    }

    // GPSLOC 테이블 insert
    public long createGPSLOC(GPSLOC gpsloc){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GPSID, gpsloc.getGPSID());
        values.put(LATITUDE, gpsloc.getLATITUDE());
        values.put(LONGITUDE, gpsloc.getLONGITUDE());


        return db.insert(TABLE_GPSLOC,null,values);
    }

    // 보이스데이타 테이블 insert
    public long createVOICEDATA(VOICEDATA voicedata){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(VOICEID, voicedata.getVOICEID());
        values.put(PAGENUM, voicedata.getPAGENUM());
        values.put(OUTPUTVOICE, voicedata.getOUTPUTVOICE());

        return db.insert(TABLE_VOICEDATA, null, values);
    }

    // all user list query
    public List<USER> getallUSER() {

        List<USER> users = new ArrayList<USER>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                USER td = new USER(
                        c.getInt(c.getColumnIndex(_ID)),
                        c.getString(c.getColumnIndex(USERID)),
                        c.getString(c.getColumnIndex(PASS)),
                        c.getString(c.getColumnIndex(NAME)),
                        c.getInt(c.getColumnIndex(AGE)),
                        c.getString(c.getColumnIndex(GENDER)),
                        c.getString(c.getColumnIndex(EMAIL)),
                        c.getString(c.getColumnIndex(PHONENUM)),
                        c.getString(c.getColumnIndex(ADDRESS))
                );

                users.add(td);
            } while (c.moveToNext());
        }

        return users;
    }

    // all OBJECT list query
    public List<OBJECT> getallOBJECT() {
        List<OBJECT> objects = new ArrayList<OBJECT>();
        String selectQuery = "SELECT  * FROM " + TABLE_OBJECT;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                OBJECT td = new OBJECT(
                        c.getInt(c.getColumnIndex(OBJECTID)),
                        c.getString(c.getColumnIndex(OBJECTNAME)),
                        c.getInt(c.getColumnIndex(PRIORITY)),
                        c.getString(c.getColumnIndex(O_ACCURACY))
                );

                objects.add(td);
            } while (c.moveToNext());
        }
        return objects;
    }

    // all BACKGROUND list qeury
    public List<BACKGROUND> getBACKGROUND(){
        List<BACKGROUND> backgrounds = new ArrayList<BACKGROUND>();
        String selectQuery = "SELECT * FROM " + TABLE_BACKGROUND;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                BACKGROUND td = new BACKGROUND(
                        c.getInt(c.getColumnIndex(BGID)),
                        c.getString(c.getColumnIndex(B_NAME)),
                        c.getString(c.getColumnIndex(B_ACCURACY))
                );
                backgrounds.add(td);
            } while (c.moveToNext());
        }
        return backgrounds;
    }

    // all GPS list qeury
    public List<GPS> getallGPS(){
        List<GPS> gpss = new ArrayList<GPS>();
        String selectQuery = "SELECT  * FROM " + TABLE_GPS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                GPS td = new GPS(
                        c.getInt(c.getColumnIndex(GPSID)),
                        c.getString(c.getColumnIndex(DATE)),
                        c.getString(c.getColumnIndex(PATHNAME)),

                        c.getString(c.getColumnIndex(STARTLATITUDE)),
                        c.getString(c.getColumnIndex(STARTLONGITUDE)),

                        c.getString(c.getColumnIndex(ENDLATITUDE)),
                        c.getString(c.getColumnIndex(ENDLONGITUDE))
                );

                gpss.add(td);
            } while (c.moveToNext());
        }
        return gpss;
    }

    // all GPSLOC list qeury
    public List<GPSLOC> getallGPSLOC(){
        List<GPSLOC> gpslocs = new ArrayList<GPSLOC>();
        String selectQuery = "SELECT * FROM " + TABLE_GPSLOC;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                GPSLOC td = new GPSLOC(
                        c.getInt(c.getColumnIndex(GPSID)),
                        c.getString(c.getColumnIndex(LATITUDE)),
                        c.getString(c.getColumnIndex(LONGITUDE))
                );
                gpslocs.add(td);
            } while (c.moveToNext());
        }
        return gpslocs;
    }

    // all VOICEDATA list qeury
    public List<VOICEDATA> getallVOICEDATA(){
        List<VOICEDATA>  voicedatas = new ArrayList<VOICEDATA>();
        String selectQuery = "SELECT * FROM " + TABLE_VOICEDATA;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                VOICEDATA td = new VOICEDATA(
                        c.getInt(c.getColumnIndex(VOICEID)),
                        c.getInt(c.getColumnIndex(PAGENUM)),
                        c.getString(c.getColumnIndex(OUTPUTVOICE))
                );
                voicedatas.add(td);
            } while (c.moveToNext());
        }
        return voicedatas;
    }

    public void deleteGPS(int deGPSID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GPS, GPSID + " = ?",
                new String[] { String.valueOf(deGPSID) });
    }

    public void deleteGPSLOC(int deGPSIDLOC){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GPSLOC, GPSID + " = ?",
                new String[] { String.valueOf(deGPSIDLOC) });
    }


    public USER searchPW(String SNAME, String SPHONE){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + NAME + " = \"" + SNAME + "\" AND " + PHONENUM + " = \"" + SPHONE+"\"";

        Cursor c = db.rawQuery(selectQuery,null);

        Log.e(LOG, selectQuery);

        if(c != null)
            c.moveToFirst();

        USER SearchUser = new USER(
                c.getInt(c.getColumnIndex(_ID)),
                c.getString(c.getColumnIndex(USERID)),
                c.getString(c.getColumnIndex(PASS)),
                c.getString(c.getColumnIndex(NAME)),
                c.getInt(c.getColumnIndex(AGE)),
                c.getString(c.getColumnIndex(GENDER)),
                c.getString(c.getColumnIndex(EMAIL)),
                c.getString(c.getColumnIndex(PHONENUM)),
                c.getString(c.getColumnIndex(ADDRESS))
        );

        return SearchUser;
    }

    public List<GPSLOC> selectPATH(String inputPathName){
        List<GPSLOC> resultGPSLOC = new ArrayList<GPSLOC>();

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT GL."+GPSID+", GL."+LATITUDE+", GL."+LONGITUDE+
                " FROM " + TABLE_GPSLOC +" as GL, " + TABLE_GPS + " as G"
                + " ON G."+GPSID + " = " + "GL." + GPSID
                + " WHERE G." + PATHNAME  + " = \"" + inputPathName + "\"";

        Cursor c = db.rawQuery(selectQuery,null);

        Log.e(LOG, selectQuery);

        if (c.moveToFirst()) {
            do {
                GPSLOC td = new GPSLOC(
                        c.getInt(c.getColumnIndex(GPSID)),
                        c.getString(c.getColumnIndex(LATITUDE)),
                        c.getString(c.getColumnIndex(LONGITUDE))
                );
                resultGPSLOC.add(td);
                Log.e(LOG, "gpsid"+ c.getString(c.getColumnIndex(GPSID))+" lati"+c.getString(c.getColumnIndex(LATITUDE))+" LONG"+c.getString(c.getColumnIndex(LONGITUDE)));
            } while (c.moveToNext());
        }

        return resultGPSLOC;
    }

    public int updateOBJECT(OBJECT todo) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(O_ACCURACY, todo.getO_ACCURACY());

        // updating row
        return db.update(TABLE_OBJECT, values, OBJECTID + " = ?",
                new String[] { String.valueOf(todo.getOBJECTID()) });
    }

    public int getToDoCount() {
        String countQuery = "SELECT * FROM " + TABLE_GPSLOC+
                " GROUP BY "+GPSID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        Log.e(LOG, countQuery);
        Log.e(LOG, "count : "+ Integer.toString(count));
        // return count
        return count;
    }

    public int loginUSER(String SID, String SPASS){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + USERID + " = \"" + SID + "\" AND " + PASS + " = \"" + SPASS+"\"";

        Cursor c = db.rawQuery(selectQuery,null);

        Log.e(LOG, selectQuery);

        int count = c.getCount();
        c.close();

        return count;
    }

    public void indexcingGPSLOC(){
        SQLiteDatabase db = this.getWritableDatabase();

        String indexingQuery ="CREATE INDEX gpslocINDEX "
                + "ON " + TABLE_GPSLOC + " (gpsid);";

        Log.e(LOG, indexingQuery);

         Cursor c = db.rawQuery(indexingQuery,null);
    }

}